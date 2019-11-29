package io.confluent.solutions.microservices.wallet.stream;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import io.confluent.solutions.microservices.wallet.model.Transaction;
import io.confluent.solutions.microservices.wallet.model.Wallet;
import io.confluent.solutions.microservices.wallet.model.WalletEntry;

public class TransactionToWalletTransformer implements ValueTransformer<Transaction, Wallet> {
	private final String walletStoreName;
	private final Lock walletStoreLock;
	private final int lockTimeoutInSeconds;

	private KeyValueStore<String, Wallet> walletStore;

	public TransactionToWalletTransformer(String walletStoreName, Lock walletStoreLock, int lockTimeoutInSeconds) {
		this.walletStoreName = walletStoreName;
		this.walletStoreLock = walletStoreLock;
		this.lockTimeoutInSeconds = lockTimeoutInSeconds;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		walletStore = (KeyValueStore<String, Wallet>) context.getStateStore(walletStoreName);
	}

	@Override
	public void close() {

	}

	@Override
	public Wallet transform(Transaction transaction) {
		try {
			if (!walletStoreLock.tryLock(lockTimeoutInSeconds, TimeUnit.SECONDS)) {
				throw new RuntimeException("Failed to obtain lock for state store.");
			}
		} catch (InterruptedException ex) {
			throw new RuntimeException("Failed to obtain lock for state store.", ex);
		}

		try {
			Wallet wallet = Optional.ofNullable(walletStore.get(transaction.getAccount()))

					.orElseGet(() -> new Wallet(transaction.getAccount(), new ArrayList<WalletEntry>()));

			if (transaction.getCreditCurrency() != null) {
				wallet = updateWalletEntryForCurrency(wallet, transaction.getCreditCurrency(),
						transaction.getCreditAmount());
			}

			if (transaction.getDebitCurrency() != null) {
				wallet = updateWalletEntryForCurrency(wallet, transaction.getDebitCurrency(),
						transaction.getDebitAmount().negate());
			}

			walletStore.put(transaction.getAccount(), wallet);

			return wallet;
		} finally {
			walletStoreLock.unlock();
		}
	}

	private Wallet updateWalletEntryForCurrency(Wallet wallet, String currency, BigDecimal amount) {
		WalletEntry updatedWalletEntry = wallet.getEntries().stream()

				.filter(walletEntry -> walletEntry.getCurrency().equals(currency)).findFirst()

				.map(existingWalletEntry -> {
					try {
						return new WalletEntry(currency, amount.add(existingWalletEntry.getBalance()));
					} catch (RuntimeException ex) {
						throw ex;
					}
				})

				.orElseGet(() -> createWalletEntry(wallet, currency, amount));

		Stream<WalletEntry> filteredWalletEntres = wallet.getEntries().stream()
				.filter(walletEntry -> !walletEntry.getCurrency().equals(currency));

		List<WalletEntry> updatedWalletEntries = Stream
				.concat(Arrays.asList(updatedWalletEntry).stream(), filteredWalletEntres).collect(Collectors.toList());

		return new Wallet(wallet.getAccount(), updatedWalletEntries);
	}

	private WalletEntry createWalletEntry(Wallet wallet, String currency, BigDecimal amount) {
		WalletEntry walletEntry = new WalletEntry(currency, amount);

		wallet.getEntries().add(walletEntry);

		return walletEntry;
	}

}
