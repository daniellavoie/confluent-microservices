package io.confluent.solutions.microservices.datafaker.domain.transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import io.confluent.solutions.microservices.datafaker.domain.rate.ProductId;
import io.confluent.solutions.microservices.datafaker.domain.transaction.Transaction.Type;
import io.confluent.solutions.microservices.datafaker.domain.wallet.Wallet;
import io.confluent.solutions.microservices.datafaker.domain.wallet.WalletEntry;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements TransactionService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);

	private static final String[] CURRENCIES = Arrays.stream(ProductId.values()).map(ProductId::name)
			.flatMap(productId -> Arrays.stream(productId.split("-"))).distinct().toArray(String[]::new);

	private static final BigDecimal MIN_WIDTHDRAW = BigDecimal.valueOf(5);
	private static final BigDecimal MAX_DEPOSIT = BigDecimal.valueOf(10000);

	private final String transactionRequestTopic;
	private final KafkaTemplate<String, Transaction> transactionTemplate;

	private final Random random = new Random();

	public TransactionServiceImpl(
			@Value("${data-faker.topics.transaction-request.name:transaction-request}") String transactionRequestTopic,
			KafkaProperties kafkaProperties) {
		this.transactionRequestTopic = transactionRequestTopic;
		this.transactionTemplate = new KafkaTemplate<>(
				new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
	}

	@Override
	public Mono<Transaction> generateRandomTransaction(Wallet wallet) {
		WalletEntry walletEntry = wallet.getEntries().size() != 0
				? wallet.getEntries().get(random.nextInt(wallet.getEntries().size()))
				: null;

		Type operationType = null;
		BigDecimal creditAmount = null;
		String creditCurrency = null;
		BigDecimal debitAmount = null;
		String debitCurrency = null;

		if (walletEntry != null && walletEntry.getBalance().compareTo(MIN_WIDTHDRAW) >= 0) {
			switch (random.nextInt(3)) {
			case 0:
				operationType = Type.DEPOSIT;
			case 1:
				operationType = Type.EXCHANGE;
			default:
				operationType = Type.WIDTHDRAW;
			}
		} else {
			operationType = Type.DEPOSIT;
		}

		if (Type.DEPOSIT.equals(operationType)) {
			creditCurrency = walletEntry != null ? walletEntry.getCurrency() : findRandomCurrency();
			creditAmount = BigDecimal.valueOf(random.nextDouble()).multiply(MAX_DEPOSIT).setScale(2,
					RoundingMode.HALF_UP);
		} else if (Type.WIDTHDRAW.equals(operationType) || Type.EXCHANGE.equals(operationType)) {
			debitCurrency = walletEntry.getCurrency();
			debitAmount = walletEntry.getBalance().add(MIN_WIDTHDRAW.negate())
					.multiply(BigDecimal.valueOf(random.nextDouble())).add(MIN_WIDTHDRAW)
					.setScale(2, RoundingMode.HALF_UP);
		}

		if (Type.EXCHANGE.equals(operationType)) {
			creditCurrency = findRandomExchangeCurrency(debitCurrency);
		}

		Transaction transaction = new Transaction(UUID.randomUUID().toString(), wallet.getAccount(), operationType,
				debitAmount, debitCurrency, creditAmount, creditCurrency);

		LOGGER.trace("Generating {}.", transaction);

		return Mono.fromFuture(transactionTemplate.send(transactionRequestTopic, wallet.getAccount(), transaction)

				.completable())

				.map(sendResult -> sendResult.getProducerRecord().value());
	}

	private String findRandomCurrency() {
		return CURRENCIES[random.nextInt(CURRENCIES.length)];
	}

	private String findRandomExchangeCurrency(String debitCurrency) {
		ProductId[] productIds = ProductId.getMatchingCurrencies(debitCurrency);

		ProductId productId = productIds[random.nextInt(productIds.length)];

		if (productId.name().startsWith(debitCurrency)) {
			return productId.name().substring(4);
		} else {
			return productId.name().substring(0, 3);
		}
	}
}
