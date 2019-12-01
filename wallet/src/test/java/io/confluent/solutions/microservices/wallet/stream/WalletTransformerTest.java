package io.confluent.solutions.microservices.wallet.stream;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.confluent.solutions.microservices.wallet.model.Transaction;
import io.confluent.solutions.microservices.wallet.model.Transaction.Type;
import io.confluent.solutions.microservices.wallet.model.Wallet;
import io.confluent.solutions.microservices.wallet.model.WalletEntry;

@ExtendWith(MockitoExtension.class)
public class WalletTransformerTest {
	private static final String DEPOSIT_ACCOUNT = "1";
	private static final String EXCHANGE_ACCOUNT = "2";
	private static final String WIDTHDRAW_ACCOUNT = "3";

	@Mock
	private KeyValueStore<String, Wallet> walletStore;

	private TransactionToWalletTransformer walletTransformer;

	@BeforeEach
	public void BeforeEach() {
		ProcessorContext context = Mockito.mock(ProcessorContext.class);

		Mockito.when(context.getStateStore("wallet")).thenReturn(walletStore);

		walletTransformer = new TransactionToWalletTransformer("wallet", new ReentrantLock(), 30);
		walletTransformer.init(context);
	}

	@Test
	public void assertDeposit() {
		Mockito.when(walletStore.get(DEPOSIT_ACCOUNT)).thenReturn(null);

		Transaction transaction = new Transaction(UUID.randomUUID().toString(), DEPOSIT_ACCOUNT, Type.DEPOSIT, null,
				null, BigDecimal.valueOf(1000), "USD");

		Wallet wallet = walletTransformer.transform(transaction);

		Assertions.assertEquals(1, wallet.getEntries().size());
		Assertions.assertEquals(1000d, wallet.getEntries().get(0).getBalance().doubleValue(), 5);
	}

	@Test
	public void assertWidthdraw() {
		Mockito.when(walletStore.get(WIDTHDRAW_ACCOUNT)).thenReturn(null);

		Transaction transaction = new Transaction(UUID.randomUUID().toString(), WIDTHDRAW_ACCOUNT, Type.WIDTHDRAW,
				BigDecimal.valueOf(1), "BTC", null, null);

		Wallet wallet = walletTransformer.transform(transaction);

		Assertions.assertEquals(1, wallet.getEntries().size());
		Assertions.assertEquals(4d, wallet.getEntries().get(0).getBalance().doubleValue(), 5);
	}

	@Test
	public void assertExchange() {
		Mockito.when(walletStore.get(EXCHANGE_ACCOUNT))
				.thenReturn(new Wallet(EXCHANGE_ACCOUNT, Arrays.asList(new WalletEntry("BTC", BigDecimal.valueOf(1d)),
						new WalletEntry("USD", BigDecimal.valueOf(500d)))));

		Transaction transaction = new Transaction(UUID.randomUUID().toString(), EXCHANGE_ACCOUNT, Type.EXCHANGE,
				BigDecimal.valueOf(200), "USD", BigDecimal.valueOf(1), "BTC");

		Wallet wallet = walletTransformer.transform(transaction);

		Assertions.assertEquals(2, wallet.getEntries().size());

		WalletEntry btcEntry = wallet.getEntries().stream()
				.filter(walletEntry -> walletEntry.getCurrency().equals("BTC")).findAny().get();
		WalletEntry usdEntry = wallet.getEntries().stream()
				.filter(walletEntry -> walletEntry.getCurrency().equals("USD")).findAny().get();

		Assertions.assertEquals(6d, btcEntry.getBalance().doubleValue(), 5);
		Assertions.assertEquals(300d, usdEntry.getBalance().doubleValue(), 5);
	}
}
