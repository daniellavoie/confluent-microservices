package io.confluent.solutions.microservices.ui.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExchangeRateDTO {
	private final String baseCurrency;
	private final String quoteCurrency;
	private final BigDecimal ask;
	private final BigDecimal bid;
	private final LocalDateTime timestamp;

	@JsonCreator
	public ExchangeRateDTO(@JsonProperty("baseCurrency") String baseCurrency,
			@JsonProperty("quoteCurrency") String quoteCurrency, @JsonProperty("ask") BigDecimal ask,
			@JsonProperty("bid") BigDecimal bid, @JsonProperty("timestamp") LocalDateTime timestamp) {
		this.baseCurrency = baseCurrency;
		this.quoteCurrency = quoteCurrency;
		this.ask = ask;
		this.bid = bid;
		this.timestamp = timestamp;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public String getQuoteCurrency() {
		return quoteCurrency;
	}

	public BigDecimal getAsk() {
		return ask;
	}

	public BigDecimal getBid() {
		return bid;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
