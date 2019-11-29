package io.confluent.solutions.microservices.datafaker.domain.rate;

import java.util.Arrays;

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

	public static ProductId[] getMatchingCurrencies(String currency) {
		return Arrays.stream(ProductId.values()).filter(productId -> productId.name().contains(currency))
				.toArray(ProductId[]::new);
	}
}
