package io.confluent.solutions.microservices.exchangerate.coinbase.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CoinbaseChannel {
	private final CoinbaseChannelName name;
	private final ProductId[] productIds;

	@JsonCreator
	public CoinbaseChannel(@JsonProperty("name") CoinbaseChannelName name,
			@JsonProperty("product_ids") ProductId[] productIds) {
		this.name = name;
		this.productIds = productIds;
	}

	public CoinbaseChannelName getName() {
		return name;
	}

	public ProductId[] getProductIds() {
		return productIds;
	}

	@Override
	public String toString() {
		return "CoinbaseChannel [name=" + name + ", productIds=" + Arrays.toString(productIds) + "]";
	}
}
