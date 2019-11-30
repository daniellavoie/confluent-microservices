package io.confluent.solutions.microservices.exchangerate.coinbase;

import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBookEvent;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.ProductId;
import reactor.core.publisher.Flux;

public interface OrderBookEventService {
	Flux<OrderBookEvent> getEvents(ProductId[] productsIds);
}
