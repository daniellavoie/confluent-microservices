package io.confluent.solutions.microservices.currencyspotter.coinbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CoinbaseMessageType {
	@JsonProperty("subscribe")
	SUBSCRIBE;
}
