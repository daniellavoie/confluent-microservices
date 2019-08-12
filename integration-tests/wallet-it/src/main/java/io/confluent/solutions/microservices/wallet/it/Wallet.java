package io.confluent.solutions.microservices.wallet.it;

import java.util.List;

public class Wallet {
	private String account;
	private List<WalletEntry> entries;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public List<WalletEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<WalletEntry> entries) {
		this.entries = entries;
	}
}
