package io.confluent.solutions.microservices.exchangerate.coinbase;

import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBookNotification;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.ProductId;
import reactor.core.publisher.Flux;

public interface OrderBookService {
	Flux<OrderBookNotification> orderBookStream(ProductId[] productIds);
}
