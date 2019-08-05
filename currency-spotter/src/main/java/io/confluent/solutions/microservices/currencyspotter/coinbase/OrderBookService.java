package io.confluent.solutions.microservices.currencyspotter.coinbase;

import io.confluent.solutions.microservices.currencyspotter.coinbase.model.OrderBookNotification;
import io.confluent.solutions.microservices.currencyspotter.coinbase.model.ProductId;
import reactor.core.publisher.Flux;

public interface OrderBookService {
	Flux<OrderBookNotification> orderBookStream(ProductId[] productIds);
}
