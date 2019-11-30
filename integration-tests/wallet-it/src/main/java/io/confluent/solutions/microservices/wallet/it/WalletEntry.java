package io.confluent.solutions.microservices.wallet.it;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WalletEntry {
	private final String currency;
	private final BigDecimal balance;

	@JsonCreator
	public WalletEntry(@JsonProperty("currency") String currency, @JsonProperty("balance") BigDecimal balance) {
		this.currency = currency;
		this.balance = balance;
	}

	public String getCurrency() {
		return currency;
	}

	public BigDecimal getBalance() {
		return balance;
	}

}
