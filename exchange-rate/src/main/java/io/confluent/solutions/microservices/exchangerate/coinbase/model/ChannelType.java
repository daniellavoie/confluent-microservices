package io.confluent.solutions.microservices.exchangerate.coinbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ChannelType {
	@JsonProperty("snapshot")
	SNAPSHOT;
}
