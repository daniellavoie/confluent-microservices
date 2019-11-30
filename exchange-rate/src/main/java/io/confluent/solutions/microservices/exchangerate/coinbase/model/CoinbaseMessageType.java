package io.confluent.solutions.microservices.exchangerate.coinbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CoinbaseMessageType {
	@JsonProperty("subscribe")
	SUBSCRIBE;
}
