package io.confluent.solutions.microservices.transaction.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountCurrencyBalance {
	private final AccountCurrency accountCurrency;
	private final BigDecimal balance;

	@JsonCreator
	public AccountCurrencyBalance(@JsonProperty("accountCurrency") AccountCurrency accountCurrency,
			@JsonProperty("balance") BigDecimal balance) {
		this.accountCurrency = accountCurrency;
		this.balance = balance;
	}

	public AccountCurrency getAccountCurrency() {
		return accountCurrency;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	@Override
	public String toString() {
		return "AccountCurrencyBalance [accountCurrency=" + accountCurrency + ", balance=" + balance + "]";
	}
}
