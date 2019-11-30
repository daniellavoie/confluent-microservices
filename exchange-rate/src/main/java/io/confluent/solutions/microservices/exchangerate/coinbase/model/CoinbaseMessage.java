package io.confluent.solutions.microservices.exchangerate.coinbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoinbaseMessage {
	private final CoinbaseMessageType type;
	private final ProductId[] productIds;
	private final CoinbaseChannelName[] channels;

	public CoinbaseMessage(CoinbaseMessageType type, ProductId[] productIds, CoinbaseChannelName[] channels) {
		this.type = type;
		this.productIds = productIds;
		this.channels = channels;
	}

	public CoinbaseMessageType getType() {
		return type;
	}

	@JsonProperty("product_ids")
	public ProductId[] getProductIds() {
		return productIds;
	}

	public CoinbaseChannelName[] getChannels() {
		return channels;
	}
}
