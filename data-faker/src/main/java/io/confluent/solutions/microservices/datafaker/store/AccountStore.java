package io.confluent.solutions.microservices.datafaker.store;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import io.confluent.solutions.microservices.datafaker.domain.account.Account;

public class AccountStore implements ValueTransformer<Account, Account> {
	private final String storeName;
	private KeyValueStore<Integer, Account> store;
	
	
	public AccountStore(String storeName) {
		this.storeName = storeName;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		store = (KeyValueStore<Integer, Account>)context.getStateStore(storeName);		
	}

	@Override
	public Account transform(Account account) {
		store.put(account.getNumber(), account);
		
		return account;
	}

	@Override
	public void close() {
	
	}
}
