package io.confluent.solutions.microservices.wallet.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Wallet {
	private final String account;
	private final List<WalletEntry> entries;

	@JsonCreator
	public Wallet(@JsonProperty("account") String account, @JsonProperty("entries") List<WalletEntry> entries) {
		this.account = account;
		this.entries = entries;
	}

	public String getAccount() {
		return account;
	}

	public List<WalletEntry> getEntries() {
		return entries;
	}

	@Override
	public String toString() {
		return "Wallet [account=" + account + ", entries=" + entries + "]";
	}
}
