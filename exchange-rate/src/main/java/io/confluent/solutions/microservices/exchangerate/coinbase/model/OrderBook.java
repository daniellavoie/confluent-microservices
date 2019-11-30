package io.confluent.solutions.microservices.exchangerate.coinbase.model;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderBook {
	private final Set<OrderBookEntry> bids;
	private final Set<OrderBookEntry> asks;

	public OrderBook() {
		asks = new ConcurrentSkipListSet<OrderBookEntry>(
				(o1, o2) -> Double.compare(o1.getPrice().doubleValue(), o2.getPrice().doubleValue()));
		bids = new ConcurrentSkipListSet<OrderBookEntry>(
				(o1, o2) -> -1 * Double.compare(o1.getPrice().doubleValue(), o2.getPrice().doubleValue()));
	}

	@JsonCreator
	public OrderBook(@JsonProperty("bids") Set<OrderBookEntry> bids, @JsonProperty("asks") Set<OrderBookEntry> asks) {
		this.bids = bids;
		this.asks = asks;
	}

	public Set<OrderBookEntry> getBids() {
		return bids;
	}

	public Set<OrderBookEntry> getAsks() {
		return asks;
	}

	@Override
	public String toString() {
		return "OrderBook [number of bids=" + bids.size() + ", number of asks=" + asks.size() + "]";
	}
}