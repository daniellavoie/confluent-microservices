package io.confluent.solutions.microservices.exchangerate.coinbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Side {
	@JsonProperty("buy")
	BUY,

	@JsonProperty("sell")
	SELL;
}
