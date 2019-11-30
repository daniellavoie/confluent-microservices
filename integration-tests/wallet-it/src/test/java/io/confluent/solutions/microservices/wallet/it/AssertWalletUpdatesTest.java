package io.confluent.solutions.microservices.wallet.it;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest(classes = WalletIntegrationTestsApplication.class)
public class AssertWalletUpdatesTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssertWalletUpdatesTest.class);

	private final String ACCOUNT = "101010";

	private Consumer<String, Wallet> walletConsumer;
	private KafkaTemplate<String, Transaction> transactionTemplate;

	@Autowired
	private KafkaProperties kafkaProperties;

	@Autowired
	private TransactionSuccessConfiguration transactionSuccessConfiguration;

	@Autowired
	private WalletConfiguration walletConfiguration;

	@BeforeEach
	public void setup() throws InterruptedException, ExecutionException {
		Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();

		consumerProperties.put("group.id", UUID.randomUUID().toString());

		walletConsumer = new DefaultKafkaConsumerFactory<String, Wallet>(consumerProperties).createConsumer();
		walletConsumer.subscribe(Arrays.asList(walletConfiguration.getName()));

		transactionTemplate = new KafkaTemplate<String, Transaction>(
				new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
	}

	@Test
	public void assertDeposit() {
		Wallet lastWallet = new Wallet(ACCOUNT, Arrays.asList());

		Iterator<ConsumerRecord<String, Wallet>> iterator;
		boolean hasMore = false;
		do {
			iterator = walletConsumer.poll(Duration.ofSeconds(5)).iterator();

			hasMore = iterator.hasNext();

			while (iterator.hasNext()) {
				Wallet wallet = iterator.next().value();
				if (wallet.getAccount().equals(ACCOUNT)) {
					lastWallet = wallet;
				}
			}

			walletConsumer.commitSync();
		} while (hasMore);

		LOGGER.info("Retreived latest {}.", lastWallet);

		walletConsumer.commitSync();

		BigDecimal creditAmount = BigDecimal.valueOf(1000d);
		String creditCurrency = "USD";
		Transaction transaction = new Transaction(UUID.randomUUID().toString(), ACCOUNT, Transaction.Type.DEPOSIT, null,
				null, creditAmount, creditCurrency);

		transactionTemplate.send(transactionSuccessConfiguration.getName(), transaction.getAccount(), transaction);
		transactionTemplate.flush();

		ConsumerRecords<String, Wallet> records = walletConsumer.poll(Duration.ofSeconds(30));
		Assertions.assertEquals(1, records.count());

		Wallet updatedWallet = records.iterator().next().value();

		WalletEntry previousUsdEntry = lastWallet.getEntries().stream()
				.filter(walletEntry -> walletEntry.getCurrency().equals(creditCurrency)).findAny()
				.orElseGet(() -> new WalletEntry(creditCurrency, BigDecimal.valueOf(0d)));
		Optional<WalletEntry> usdEntry = updatedWallet.getEntries().stream()
				.filter(walletEntry -> walletEntry.getCurrency().equals(creditCurrency)).findAny();

		Assertions.assertTrue(usdEntry.isPresent());
		Assertions.assertEquals(0,
				usdEntry.get().getBalance().compareTo(previousUsdEntry.getBalance().add(creditAmount)));
	}
}
