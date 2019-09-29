package io.confluent.solutions.microservices.currencyspotter.coinbase.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderBookEntry {
	private final BigDecimal price;
	private final BigDecimal size;
	private final Side side;

	@JsonCreator
	public OrderBookEntry(@JsonProperty("price") BigDecimal price, @JsonProperty("size") BigDecimal size,
			@JsonProperty("side") Side side) {
		this.price = price.setScale(8);
		this.size = size;
		this.side = side;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getSize() {
		return size;
	}

	public Side getSide() {
		return side;
	}

	@Override
	public String toString() {
		return "OrderBookEntry [price=" + price + ", size=" + size + ", side=" + side + "]";
	}
}
