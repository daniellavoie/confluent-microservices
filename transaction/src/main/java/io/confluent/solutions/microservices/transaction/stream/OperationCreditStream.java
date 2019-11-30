package io.confluent.solutions.microservices.transaction.stream;

import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.confluent.solutions.microservices.transaction.model.OperationResult;
import io.confluent.solutions.microservices.transaction.model.Transaction;

public class OperationCreditStream {
	private static final Logger LOGGER = LoggerFactory.getLogger(OperationCreditStream.class);

	public static KStream<String, OperationResult> build(KStream<String, Transaction> transactionRequestStream,
			KStream<String, OperationResult> operationDebitStream, String accountCurrencyBalanceStoreName,
			String rateStoreName) {
		return transactionRequestStream

				.filter(OperationCreditStream::filterDepositTransaction)

				.merge(operationDebitStream

						.filter((account, debitOperationResult) -> debitOperationResult.isSuccess())

						.mapValues(OperationResult::getTransaction)

						.filter((account, transaction) -> Transaction.Type.EXCHANGE.equals(transaction.getType())))

				.transformValues(() -> new OperationCreditTransformer(accountCurrencyBalanceStoreName, rateStoreName),
						accountCurrencyBalanceStoreName)

				.peek(OperationCreditStream::traceOperationResult);
	}

	private static boolean filterDepositTransaction(String transactionKey, Transaction transaction) {
		return Transaction.Type.DEPOSIT.equals(transaction.getType());
	}

	private static void traceOperationResult(String account, OperationResult operationResult) {
		LOGGER.trace("Operation credit stream notifying {}.", operationResult);
	}
}
