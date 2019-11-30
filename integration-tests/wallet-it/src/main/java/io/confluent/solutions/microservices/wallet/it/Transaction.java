package io.confluent.solutions.microservices.wallet.it;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {
	public enum Type {
		DEPOSIT, EXCHANGE, WIDTHDRAW
	}

	private final String guid;
	private final String account;
	private final Type type;
	private final BigDecimal debitAmount;
	private final String debitCurrency;
	private final BigDecimal creditAmount;
	private final String creditCurrency;

	@JsonCreator
	public Transaction(@JsonProperty("guid") String guid, @JsonProperty("account") String account,
			@JsonProperty("type") Type type, @JsonProperty("debitAmount") BigDecimal debitAmount,
			@JsonProperty("debitCurrency") String debitCurrency, @JsonProperty("creditAmount") BigDecimal creditAmount,
			@JsonProperty("creditCurrency") String creditCurrency) {
		this.guid = guid;
		this.account = account;
		this.type = type;
		this.debitAmount = debitAmount;
		this.debitCurrency = debitCurrency;
		this.creditAmount = creditAmount;
		this.creditCurrency = creditCurrency;
	}

	public String getGuid() {
		return guid;
	}

	public String getAccount() {
		return account;
	}

	public Type getType() {
		return type;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public String getDebitCurrency() {
		return debitCurrency;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public String getCreditCurrency() {
		return creditCurrency;
	}
}
