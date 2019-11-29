package io.confluent.solutions.microservices.transaction.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountCurrency {
	private final String account;
	private final String currency;

	@JsonCreator
	public AccountCurrency(@JsonProperty("account") String account, @JsonProperty("currency") String currency) {
		this.account = account;
		this.currency = currency;
	}

	public String getAccount() {
		return account;
	}

	public String getCurrency() {
		return currency;
	}

	@Override
	public String toString() {
		return "AccountCurrency [account=" + account + ", currency=" + currency + "]";
	}

	public String asKey() {
		return account + "-" + currency;
	}
}
