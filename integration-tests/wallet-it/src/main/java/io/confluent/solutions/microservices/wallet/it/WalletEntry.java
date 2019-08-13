package io.confluent.solutions.microservices.wallet.it;

import java.math.BigDecimal;

public class WalletEntry {
	private String currency;
	private BigDecimal balance;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}
