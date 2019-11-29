package io.confluent.solutions.microservices.wallet.stream;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import io.confluent.solutions.microservices.wallet.model.Account;
import io.confluent.solutions.microservices.wallet.model.Wallet;
import io.confluent.solutions.microservices.wallet.model.WalletEntry;

public class AccountToWalletTransformer implements ValueTransformer<Account, Wallet> {
	private final String walletStoreName;
	private final Lock walletStoreLock;
	private final int lockTimeoutInSeconds;

	private KeyValueStore<String, Wallet> walletStore;

	public AccountToWalletTransformer(String walletStoreName, Lock walletStoreLock, int lockTimeoutInSeconds) {
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
	public Wallet transform(Account account) {
		try {
			if (!walletStoreLock.tryLock(lockTimeoutInSeconds, TimeUnit.SECONDS)) {
				throw new RuntimeException("Failed to obtain lock for state store.");
			}
		} catch (InterruptedException ex) {
			throw new RuntimeException("Failed to obtain lock for state store.", ex);
		}

		try {
			String accountNumber = String.valueOf(account.getNumber());

			return Optional.ofNullable(walletStore.get(accountNumber))

					.orElseGet(() -> createNewWallet(accountNumber));
		} finally {
			walletStoreLock.unlock();
		}
	}

	private Wallet createNewWallet(String account) {
		Wallet wallet = new Wallet(account, new ArrayList<WalletEntry>());

		walletStore.put(account, wallet);

		return wallet;
	}

}
