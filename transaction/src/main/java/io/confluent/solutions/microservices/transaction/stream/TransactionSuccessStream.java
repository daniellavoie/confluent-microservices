package io.confluent.solutions.microservices.transaction.stream;

import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.confluent.solutions.microservices.transaction.model.OperationResult;
import io.confluent.solutions.microservices.transaction.model.Transaction;
import io.confluent.solutions.microservices.transaction.topic.TransactionSuccessConfiguration;

public abstract class TransactionSuccessStream {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionSuccessStream.class);

	public static KStream<String, Transaction> build(KStream<String, OperationResult> operationCreditStream,
			KStream<String, OperationResult> operationDebitStream, TransactionSuccessConfiguration topicConfiguration) {
		return successfulDepositAndExhchangeOperations(operationCreditStream)

				.merge(successfulWidthdrawOperations(operationDebitStream))

				.mapValues(OperationResult::getTransaction)

				.peek(TransactionSuccessStream::traceTransaction)

				.through(topicConfiguration.getName());
	}

	private static KStream<String, OperationResult> successfulDepositAndExhchangeOperations(
			KStream<String, OperationResult> operationCreditStream) {
		return operationCreditStream

				.filter(TransactionSuccessStream::filterCreditOperationResult);
	}

	private static KStream<String, OperationResult> successfulWidthdrawOperations(
			KStream<String, OperationResult> operationDebitStream) {
		return operationDebitStream

				.filter(TransactionSuccessStream::filterDebitOperationResult);
	}

	private static boolean filterCreditOperationResult(String account, OperationResult operationResult) {
		return operationResult.isSuccess()
				&& (Transaction.Type.DEPOSIT.equals(operationResult.getTransaction().getType())
						|| Transaction.Type.EXCHANGE.equals(operationResult.getTransaction().getType()));
	}

	private static boolean filterDebitOperationResult(String account, OperationResult operationResult) {
		return operationResult.isSuccess()
				&& Transaction.Type.WIDTHDRAW.equals(operationResult.getTransaction().getType());
	}

	private static void traceTransaction(String account, Transaction transaction) {
		LOGGER.trace("Notifying successful {}.", transaction);
	}
}
