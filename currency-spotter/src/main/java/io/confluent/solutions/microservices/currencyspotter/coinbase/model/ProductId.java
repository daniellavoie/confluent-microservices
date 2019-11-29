package io.confluent.solutions.microservices.currencyspotter.coinbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProductId {
	@JsonProperty("BTC-USD")
	BTC_USD,

	@JsonProperty("BTC-EUR")
	BTC_EUR,

	@JsonProperty("BTC-GBP")
	BTC_GBP,

	@JsonProperty("ETH-EUR")
	ETH_EUR,

	@JsonProperty("ETH-USD")
	ETH_USD,

	@JsonProperty("ETH-GBP")
	ETH_GPB,

	@JsonProperty("ETH-BTC")
	ETH_BTC;
}
