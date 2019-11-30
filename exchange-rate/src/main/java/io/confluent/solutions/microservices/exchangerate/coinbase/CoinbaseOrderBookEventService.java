package io.confluent.solutions.microservices.exchangerate.coinbase;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBookEvent;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.ProductId;
import reactor.core.publisher.Flux;

public class CoinbaseOrderBookEventService implements OrderBookEventService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CoinbaseOrderBookEventService.class);

	private final CoinbaseConfiguration coinbaseConfiguration;

	private final ReactorNettyWebSocketClient webSocketClient = new ReactorNettyWebSocketClient();

	public CoinbaseOrderBookEventService(CoinbaseConfiguration coinbaseConfiguration) {
		this.coinbaseConfiguration = coinbaseConfiguration;

		// Snapshots payload from coinbase are huuuuuuuuuuuuuge.
		webSocketClient.setMaxFramePayloadLength(65536 * 10);

	}

	@Override
	public Flux<OrderBookEvent> getEvents(ProductId[] productsIds) {
		return Flux.create(sink -> webSocketClient

				.execute(URI.create(coinbaseConfiguration.getWebsocketUrl()),
						new CoinbaseWebSocketHandler(productsIds, sink))

				.doOnError(ex -> LOGGER.error("Error while subscribing to coinbase.", ex))

				.doOnError(sink::error)

				.doOnSubscribe(subscriber -> LOGGER.info("Subscribing to coinbase websocket API."))

				.subscribe());
	}
}
