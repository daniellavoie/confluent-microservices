package io.confluent.solutions.microservices.currencyspotter.coinbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ChannelType {
	@JsonProperty("snapshot")
	SNAPSHOT;
}
