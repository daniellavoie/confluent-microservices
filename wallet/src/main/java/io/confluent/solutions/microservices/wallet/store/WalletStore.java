package io.confluent.solutions.microservices.wallet.store;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.apache.kafka.streams.processor.StateStore;

public class WalletStore {
	private int lockTimeoutInSeconds;

	public WalletStore(int lockTimeoutInSeconds) {
		this.lockTimeoutInSeconds = lockTimeoutInSeconds;
	}

	private Lock lock = new ReentrantLock();

	public void applyStoreOperation(Consumer<StateStore> consumer, StateStore stateStore) throws InterruptedException {
		if (!lock.tryLock(lockTimeoutInSeconds, TimeUnit.SECONDS)) {
			throw new InterruptedException("Failed to obtain lock for state store.");
		}

		try {
			consumer.accept(stateStore);
		} finally {
			lock.unlock();
		}

	}
}
