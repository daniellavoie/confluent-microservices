package io.confluent.solutions.microservices.it;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
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

import io.confluent.solutions.microservices.transaction.Transaction;

@SpringBootTest
public class AssertTransactions {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssertTransactions.class);

	private Consumer<String, Transaction> successTransaction;
	private KafkaTemplate<String, Transaction> transactionTemplate;

	@Autowired
	private KafkaProperties kafkaProperties;

	@BeforeEach
	public void setup() {
		successTransaction = new DefaultKafkaConsumerFactory<String, Transaction>(
				kafkaProperties.buildConsumerProperties()).createConsumer();

		successTransaction.subscribe(Arrays.asList("transaction-success"));

		transactionTemplate = new KafkaTemplate<String, Transaction>(
				new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
	}

	@AfterEach
	public void tearDown() {
		successTransaction.close();
	}

	@Test
	public void assertDepositTransaction() {
		generateDeposit(10, "USD");
	}

	@Test
	public void assertWidthdrawTransaction() {
		generateDeposit(10, "USD");
		generateWithdraw(5d, "USD");
	}

	@Test
	public void assertExchangeTransaction() {
		generateDeposit(1000, "USD");
		
		Transaction exchangeTransaction = generateExchange(1000d, "USD", "BTC");

		Assertions.assertNotNull(exchangeTransaction.getCreditAmount());
		
		generateDeposit(2, "BTC");
		
		exchangeTransaction = generateExchange(1, "BTC", "USD");

		Assertions.assertNotNull(exchangeTransaction.getCreditAmount());
	}

	private Transaction generateExchange(double amount, String debitCurrency, String creditCurrency) {
		return generateTransaction(amount, debitCurrency, null, creditCurrency, Transaction.Type.EXCHANGE);
	}

	private Transaction generateDeposit(double amount, String currency) {
		return generateTransaction(null, null, amount, currency, Transaction.Type.DEPOSIT);
	}

	private Transaction generateWithdraw(double amount, String currency) {
		return generateTransaction(amount, currency, null, null, Transaction.Type.WIDTHDRAW);
	}

	private Transaction generateTransaction(Double debitAmount, String debitCurrency, Double creditAmount,
			String creditCurrency, Transaction.Type type) {
		Transaction expectedTransaction = new Transaction(UUID.randomUUID().toString(), "1", type,
				debitAmount != null ? BigDecimal.valueOf(debitAmount) : null, debitCurrency,
				creditAmount != null ? BigDecimal.valueOf(creditAmount) : null, creditCurrency);

		LOGGER.info("Drained {} events from topic.", successTransaction.poll(Duration.ofSeconds(5)).count());

		successTransaction.commitSync();

		transactionTemplate.send("transaction-request", expectedTransaction.getAccount(), expectedTransaction);
		transactionTemplate.flush();

		ConsumerRecords<String, Transaction> records = successTransaction.poll(Duration.ofSeconds(30));
		Assertions.assertEquals(1, records.count());

		Transaction transaction = records.iterator().next().value();

		Assertions.assertEquals(expectedTransaction.getGuid(), transaction.getGuid());

		return transaction;
	}
}
