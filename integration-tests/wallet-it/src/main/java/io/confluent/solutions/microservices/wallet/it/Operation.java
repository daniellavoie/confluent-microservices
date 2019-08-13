package io.confluent.solutions.microservices.wallet.it;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Operation {
	private String account;
	private String type;
	private BigDecimal debitAmount;
	private String debitCurrency;
	private BigDecimal creditAmount;
	private String creditCurrency;

	public Operation() {
	}

	public Operation(String account, String type, BigDecimal debitAmount, String debitCurrency, BigDecimal creditAmount,
			String creditCurrency) {
		this.account = account;
		this.type = type;
		this.debitAmount = debitAmount;
		this.debitCurrency = debitCurrency;
		this.creditAmount = creditAmount;
		this.creditCurrency = creditCurrency;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	public String getDebitCurrency() {
		return debitCurrency;
	}

	public void setDebitCurrency(String debitCurrency) {
		this.debitCurrency = debitCurrency;
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public String getCreditCurrency() {
		return creditCurrency;
	}

	public void setCreditCurrency(String creditCurrency) {
		this.creditCurrency = creditCurrency;
	}
}
