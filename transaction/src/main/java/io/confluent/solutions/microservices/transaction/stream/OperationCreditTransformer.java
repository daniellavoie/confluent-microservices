package io.confluent.solutions.microservices.transaction.stream;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import io.confluent.solutions.microservices.exchangerate.ExchangeRate;
import io.confluent.solutions.microservices.transaction.model.AccountCurrency;
import io.confluent.solutions.microservices.transaction.model.AccountCurrencyBalance;
import io.confluent.solutions.microservices.transaction.model.OperationResult;
import io.confluent.solutions.microservices.transaction.model.Transaction;

public class OperationCreditTransformer extends OperationTransformer {
	private final String balanceStateStoreName;
	private final String rateStoreName;

	private KeyValueStore<String, AccountCurrencyBalance> balanceStore;
	private KeyValueStore<String, ExchangeRate> rateStore;

	public OperationCreditTransformer(String balanceStateStoreName, String rateStoreName) {
		this.balanceStateStoreName = balanceStateStoreName;
		this.rateStoreName = rateStoreName;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		balanceStore = (KeyValueStore<String, AccountCurrencyBalance>) context.getStateStore(balanceStateStoreName);
		rateStore = (KeyValueStore<String, ExchangeRate>) context.getStateStore(rateStoreName);
	}

	@Override
	public void close() {

	}

	@Override
	public OperationResult transform(Transaction transaction) {
		String error = validateOperation(transaction);

		if (error != null) {
			return new OperationResult(false, transaction, null, error);
		}

		BigDecimal creditAmount = transaction.getCreditAmount();
		if (Transaction.Type.EXCHANGE.equals(transaction.getType())) {
			Optional<ExchangeRate> exchangeRate = Optional
					.ofNullable(rateStore.get(transaction.getCreditCurrency() + "-" + transaction.getDebitCurrency()));
			Optional<ExchangeRate> invertedExchangeRate = Optional
					.ofNullable(rateStore.get(transaction.getDebitCurrency() + "-" + transaction.getCreditCurrency()));

			ExchangeRate rate = exchangeRate.orElse(invertedExchangeRate.orElse(null));
			if (rate == null) {
				return new OperationResult(false, transaction, null, "Exchange rate could not be found for "
						+ transaction.getCreditCurrency() + " and " + transaction.getDebitCurrency());
			}

			if (rate.getBaseCurrency().equals(transaction.getCreditCurrency())) {
				creditAmount = transaction.getDebitAmount().multiply(rate.getBid());
			} else {
				creditAmount = transaction.getDebitAmount().divide(rate.getAsk(), 5, RoundingMode.HALF_UP);
			}
		}

		String storeKey = transaction.getAccount() + "-" + transaction.getCreditCurrency();

		AccountCurrencyBalance existingBalance = Optional.ofNullable(balanceStore.get(storeKey))
				.orElseGet(() -> new AccountCurrencyBalance(
						new AccountCurrency(transaction.getAccount(), transaction.getCreditCurrency()),
						BigDecimal.ZERO));

		AccountCurrencyBalance newBalance = new AccountCurrencyBalance(existingBalance.getAccountCurrency(),
				existingBalance.getBalance().add(creditAmount));

		balanceStore.put(storeKey, newBalance);

		return new OperationResult(true,
				new Transaction(transaction.getGuid(), transaction.getAccount(), transaction.getType(),
						transaction.getDebitAmount(), transaction.getDebitCurrency(), creditAmount,
						transaction.getCreditCurrency()),
				newBalance, null);
	}

}
