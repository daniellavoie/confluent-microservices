package io.confluent.solutions.microservices.operation.at;

import java.math.BigDecimal;

public class FundRequest {
	private String account;
	private String action;
	private String currency;
	private BigDecimal amount;
	private String bankAccount;
	private String creditCardNumber;

	public FundRequest() {
	}

	public FundRequest(String account, String action, String currency, BigDecimal amount, String bankAccount,
			String creditCardNumber) {
		this.account = account;
		this.action = action;
		this.currency = currency;
		this.amount = amount;
		this.bankAccount = bankAccount;
		this.creditCardNumber = creditCardNumber;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}
}
