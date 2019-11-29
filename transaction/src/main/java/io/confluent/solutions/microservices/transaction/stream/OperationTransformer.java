package io.confluent.solutions.microservices.transaction.stream;

import java.math.BigDecimal;

import org.apache.kafka.streams.kstream.ValueTransformer;

import io.confluent.solutions.microservices.transaction.model.OperationResult;
import io.confluent.solutions.microservices.transaction.model.Transaction;

public abstract class OperationTransformer implements ValueTransformer<Transaction, OperationResult> {
	protected String validateOperation(Transaction transaction) {
		String error = null;
		if (transaction.getGuid() == null) {
			error = "Guid is undefined";
		} else if (transaction.getAccount() == null) {
			error = "Account is undefined";
		} else if (transaction.getType() == null) {
			error = "Transaction type is undefined";
		}

		if ((Transaction.Type.DEPOSIT.equals(transaction.getType())
				|| Transaction.Type.EXCHANGE.equals(transaction.getType()))
				&& transaction.getCreditCurrency() == null) {
			error = "Undefined credit currency";

		} else if (Transaction.Type.DEPOSIT.equals(transaction.getType()) && transaction.getCreditAmount() == null) {
			error = "Invalid credit amount";

		} else if (Transaction.Type.WIDTHDRAW.equals(transaction.getType())
				|| Transaction.Type.EXCHANGE.equals(transaction.getType())) {
			if (transaction.getDebitAmount() == null || transaction.getDebitAmount().compareTo(BigDecimal.ZERO) <= 0) {
				error = "Invalid debit amount";
			} else if (transaction.getDebitCurrency() == null) {
				error = "Undefined debit currency";
			}
		}

		return error;
	}
}
