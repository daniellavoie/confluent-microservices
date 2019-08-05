package io.confluent.solutions.microservices.currencyspotter.coinbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderBookEventType {
	@JsonProperty("l2update")
	L2_UPDATE,

	@JsonProperty("snapshot")
	SNAPSHOT,

	@JsonProperty("subscriptions")
	SUBSCRIPTIONS,
	
	@JsonProperty("ticker")
	TICKER;

}
