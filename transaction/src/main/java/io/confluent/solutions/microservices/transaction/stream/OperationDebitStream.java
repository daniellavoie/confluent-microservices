package io.confluent.solutions.microservices.transaction.stream;

import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.confluent.solutions.microservices.transaction.model.OperationResult;
import io.confluent.solutions.microservices.transaction.model.Transaction;

public class OperationDebitStream {
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationDebitStream.class);

	public static KStream<String, OperationResult> build(KStream<String, Transaction> transactionRequestStream,
			String accountCurrencyBalanceStoreName) {

		return transactionRequestStream

				.filter(OperationDebitStream::filterOnTransactionType)

				.transformValues(() -> new OperationDebitTransformer(accountCurrencyBalanceStoreName),
						accountCurrencyBalanceStoreName)

				.peek(OperationDebitStream::traceOperationResult);
	}

	private static boolean filterOnTransactionType(String account, Transaction transaction) {
		LOGGER.trace("Applying operation type filter for {}.", transaction);

		return Transaction.Type.WIDTHDRAW.equals(transaction.getType())
				|| Transaction.Type.EXCHANGE.equals(transaction.getType());
	}

	private static void traceOperationResult(String account, OperationResult debitOperationResult) {
		if (debitOperationResult.isSuccess()) {
			LOGGER.trace("Debited {} {} from {}.", debitOperationResult.getTransaction().getDebitAmount(),
					debitOperationResult.getTransaction().getDebitCurrency(),
					debitOperationResult.getTransaction().getAccount());
		} else {
			LOGGER.trace("Rejected debit of {} {} from {}.", debitOperationResult.getTransaction().getDebitAmount(),
					debitOperationResult.getTransaction().getDebitCurrency(),
					debitOperationResult.getTransaction().getAccount());
		}

		LOGGER.trace("Current balance for {} is {} {}.", debitOperationResult.getTransaction().getAccount(),
				debitOperationResult.getAccountCurrencyBalance() != null
						? debitOperationResult.getAccountCurrencyBalance().getBalance()
						: null,
				debitOperationResult.getAccountCurrencyBalance() != null
						? debitOperationResult.getAccountCurrencyBalance().getAccountCurrency().getCurrency()
						: null);
	}
}
