package io.confluent.solutions.microservices.currencyspotter.at.exchangerate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExchangeRate {
	private Currency baseCurrency;
	private Currency quoteCurrency;
	private BigDecimal ask;
	private BigDecimal bid;
	private LocalDateTime timestamp;

	public ExchangeRate() {

	}

	public ExchangeRate(Currency baseCurrency, Currency quoteCurrency, BigDecimal ask, BigDecimal bid,
			LocalDateTime timestamp) {
		super();
		this.baseCurrency = baseCurrency;
		this.quoteCurrency = quoteCurrency;
		this.ask = ask;
		this.bid = bid;
		this.timestamp = timestamp;
	}

	public Currency getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(Currency baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public Currency getQuoteCurrency() {
		return quoteCurrency;
	}

	public void setQuoteCurrency(Currency quoteCurrency) {
		this.quoteCurrency = quoteCurrency;
	}

	public BigDecimal getAsk() {
		return ask;
	}

	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}

	public BigDecimal getBid() {
		return bid;
	}

	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
