package io.confluent.solutions.microservices.transaction.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OperationResult {
	private final boolean success;
	private final Transaction transaction;
	private final AccountCurrencyBalance accountCurrencyBalance;
	private final String error;

	@JsonCreator
	public OperationResult(@JsonProperty("success") boolean success,
			@JsonProperty("transaction") Transaction transaction,
			@JsonProperty("accountCurrencyBalance") AccountCurrencyBalance accountCurrencyBalance,
			@JsonProperty("error") String error) {
		this.success = success;
		this.transaction = transaction;
		this.accountCurrencyBalance = accountCurrencyBalance;
		this.error = error;
	}

	public boolean isSuccess() {
		return success;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public AccountCurrencyBalance getAccountCurrencyBalance() {
		return accountCurrencyBalance;
	}

	public String getError() {
		return error;
	}

	@Override
	public String toString() {
		return "OperationResult [success=" + success + ", transaction=" + transaction + ", accountCurrencyBalance="
				+ accountCurrencyBalance + ", error=" + error + "]";
	}
}
