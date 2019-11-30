package io.confluent.solutions.microservices.exchangerate.coinbase.converter;

import java.math.BigDecimal;

import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBookEntry;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.Side;

public abstract class PriceConverter {
	public static OrderBookEntry convertToOrderBookEntry(String[] price, Side side) {
		return new OrderBookEntry(new BigDecimal(price[0]), new BigDecimal(price[1]), side);
	}
}
