package io.confluent.solutions.microservices.currencyspotter.coinbase;

import io.confluent.solutions.microservices.currencyspotter.coinbase.model.OrderBookEvent;
import io.confluent.solutions.microservices.currencyspotter.coinbase.model.ProductId;
import reactor.core.publisher.Flux;

public interface OrderBookEventService {
	Flux<OrderBookEvent> getEvents(ProductId[] productsIds);
}
