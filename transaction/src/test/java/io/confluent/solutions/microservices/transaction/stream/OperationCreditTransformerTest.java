package io.confluent.solutions.microservices.transaction.stream;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import io.confluent.solutions.microservices.exchangerate.ExchangeRate;
import io.confluent.solutions.microservices.transaction.TransactionApplication;
import io.confluent.solutions.microservices.transaction.model.AccountCurrency;
import io.confluent.solutions.microservices.transaction.model.AccountCurrencyBalance;
import io.confluent.solutions.microservices.transaction.model.OperationResult;
import io.confluent.solutions.microservices.transaction.model.Transaction;
import io.confluent.solutions.microservices.transaction.model.Transaction.Type;

@SpringBootTest(classes = TransactionApplication.class)
public class OperationCreditTransformerTest {
	private static final String ACCOUNT = "1";
	private static final String BALANCE_STORE_NAME = "balance-store";
	private static final String RATE_STORE = "rate-store";

	private OperationCreditTransformer operationCreditTransformer;

	@Mock
	private KeyValueStore<String, AccountCurrencyBalance> balanceStore;

	@Mock
	private KeyValueStore<String, ExchangeRate> rateStore;

	@Mock
	private ProcessorContext context;

	@BeforeEach
	public void setup() {
		operationCreditTransformer = new OperationCreditTransformer(BALANCE_STORE_NAME, RATE_STORE);

		Mockito.when(context.getStateStore(BALANCE_STORE_NAME)).thenReturn(balanceStore);
		Mockito.when(context.getStateStore(RATE_STORE)).thenReturn(rateStore);

		operationCreditTransformer.init(context);
	}

	@Test
	public void assertExchange() {
		String debitCurrency = "USD";
		BigDecimal debitAmount = BigDecimal.valueOf(800d);
		String creditCurrency = "BTC";

		BigDecimal askPrice = BigDecimal.valueOf(8222.21d);
		BigDecimal bidPrice = BigDecimal.valueOf(8219.04d);
		BigDecimal initialCreditCurrencyBalance = BigDecimal.valueOf(0.2d);

		BigDecimal expectedCreditAmount = debitAmount.divide(askPrice, 5, RoundingMode.HALF_UP);

		Mockito.when(balanceStore.get(ACCOUNT + "-" + creditCurrency)).thenReturn(
				new AccountCurrencyBalance(new AccountCurrency(ACCOUNT, creditCurrency), initialCreditCurrencyBalance));

		Mockito.when(rateStore.get(debitCurrency + "-" + creditCurrency)).thenReturn(new ExchangeRate(debitCurrency,
				creditCurrency, askPrice, bidPrice, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)));

		Transaction transaction = new Transaction(UUID.randomUUID().toString(), ACCOUNT, Type.EXCHANGE, debitAmount,
				debitCurrency, null, creditCurrency);

		OperationResult operationResult = operationCreditTransformer.transform(transaction);

		Assertions.assertEquals(0, expectedCreditAmount.compareTo(operationResult.getTransaction().getCreditAmount()));
		Assertions.assertEquals(0, initialCreditCurrencyBalance.add(expectedCreditAmount)
				.compareTo(operationResult.getAccountCurrencyBalance().getBalance()));
	}

	@Test
	public void assertExchangeWithInvertedRate() {
		String debitCurrency = "BTC";
		BigDecimal debitAmount = BigDecimal.valueOf(0.5d);
		String creditCurrency = "USD";

		BigDecimal askPrice = BigDecimal.valueOf(8222.21d);
		BigDecimal bidPrice = BigDecimal.valueOf(8219.04d);
		BigDecimal initialCreditCurrencyBalance = BigDecimal.valueOf(2000d);

		BigDecimal expectedCreditAmount = debitAmount.multiply(bidPrice);

		Mockito.when(balanceStore.get(ACCOUNT + "-" + creditCurrency)).thenReturn(
				new AccountCurrencyBalance(new AccountCurrency(ACCOUNT, creditCurrency), initialCreditCurrencyBalance));

		Mockito.when(rateStore.get(debitCurrency + "-" + creditCurrency)).thenReturn(new ExchangeRate(creditCurrency,
				debitCurrency, askPrice, bidPrice, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)));

		Transaction transaction = new Transaction(UUID.randomUUID().toString(), ACCOUNT, Type.EXCHANGE, debitAmount,
				debitCurrency, null, creditCurrency);

		OperationResult operationResult = operationCreditTransformer.transform(transaction);

		Assertions.assertEquals(0, expectedCreditAmount.compareTo(operationResult.getTransaction().getCreditAmount()));
		Assertions.assertEquals(0, initialCreditCurrencyBalance.add(expectedCreditAmount)
				.compareTo(operationResult.getAccountCurrencyBalance().getBalance()));
	}
}
