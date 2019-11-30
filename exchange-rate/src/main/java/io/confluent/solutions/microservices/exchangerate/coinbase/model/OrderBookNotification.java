package io.confluent.solutions.microservices.exchangerate.coinbase.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderBookNotification {
	private final OrderBook orderBook;
	private final OrderBookEvent orderBookEvent;

	@JsonCreator
	public OrderBookNotification(@JsonProperty("orderBook") OrderBook orderBook, @JsonProperty("orderBookEvent") OrderBookEvent orderBookEvent) {
		this.orderBook = orderBook;
		this.orderBookEvent = orderBookEvent;
	}

	public OrderBook getOrderBook() {
		return orderBook;
	}

	public OrderBookEvent getOrderBookEvent() {
		return orderBookEvent;
	}

	@Override
	public String toString() {
		return "OrderBookNotification [orderBook=" + orderBook + ", orderBookEvent=" + orderBookEvent + "]";
	}
}
