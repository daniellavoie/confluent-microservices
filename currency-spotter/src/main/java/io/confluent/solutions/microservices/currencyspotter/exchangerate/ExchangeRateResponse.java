package io.confluent.solutions.microservices.currencyspotter.exchangerate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExchangeRateResponse {
	private String baseCurrency;
	private String quoteCurrency;
	private BigDecimal ask;
	private BigDecimal bid;
	private LocalDateTime timestamp;

	public ExchangeRateResponse() {

	}

	public ExchangeRateResponse(String baseCurrency, String quoteCurrency, BigDecimal ask, BigDecimal bid,
			LocalDateTime timestamp) {
		this.baseCurrency = baseCurrency;
		this.quoteCurrency = quoteCurrency;
		this.ask = ask;
		this.bid = bid;
		this.timestamp = timestamp;

	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public String getQuoteCurrency() {
		return quoteCurrency;
	}

	public void setQuoteCurrency(String quoteCurrency) {
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
