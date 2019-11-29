package io.confluent.solutions.microservices.transaction.stream;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import io.confluent.solutions.microservices.transaction.model.AccountCurrency;
import io.confluent.solutions.microservices.transaction.model.AccountCurrencyBalance;
import io.confluent.solutions.microservices.transaction.model.OperationResult;
import io.confluent.solutions.microservices.transaction.model.Transaction;

public class OperationDebitTransformer extends OperationTransformer {
	private String stateStoreName;
	private KeyValueStore<String, AccountCurrencyBalance> store;

	public OperationDebitTransformer(String stateStoreName) {
		this.stateStoreName = stateStoreName;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		store = (KeyValueStore<String, AccountCurrencyBalance>) context.getStateStore(stateStoreName);
	}

	@Override
	public OperationResult transform(Transaction transaction) {
		String error = validateOperation(transaction);

		if (error != null) {
			return new OperationResult(false, transaction, null, error);
		}

		String storeKey = transaction.getAccount() + "-" + transaction.getDebitCurrency();

		AccountCurrencyBalance existingBalance = Optional.ofNullable(store.get(storeKey))
				.orElseGet(() -> new AccountCurrencyBalance(
						new AccountCurrency(transaction.getAccount(), transaction.getCreditCurrency()),
						BigDecimal.ZERO));

		if (transaction.getDebitAmount() == null || transaction.getDebitCurrency() == null
				|| existingBalance.getBalance().compareTo(transaction.getDebitAmount()) < 0) {
			return new OperationResult(false, transaction, existingBalance, "Insufisant funds");
		}

		AccountCurrencyBalance newBalance = new AccountCurrencyBalance(existingBalance.getAccountCurrency(),
				existingBalance.getBalance().add(transaction.getDebitAmount().negate()));

		store.put(storeKey, newBalance);

		return new OperationResult(true, transaction, newBalance, null);

	}

	@Override
	public void close() {

	}
}
