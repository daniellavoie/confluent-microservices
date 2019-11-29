package io.confluent.solutions.microservices.transaction.stream;

import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.confluent.solutions.microservices.transaction.model.OperationResult;
import io.confluent.solutions.microservices.transaction.model.Transaction;
import io.confluent.solutions.microservices.transaction.topic.TransactionFailedConfiguration;

public abstract class TransactionFailedStream {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionFailedStream.class);

	public static KStream<String, Transaction> build(TransactionFailedConfiguration topicConfiguration,
			KStream<String, OperationResult> operationDebitStream) {
		return operationDebitStream.filter((account, debitOperationResult) -> !debitOperationResult.isSuccess())

				.mapValues(OperationResult::getTransaction)

				.peek((string, transaction) -> LOGGER.info("Rejected {}.", transaction))

				.through(topicConfiguration.getName());
	}
}
