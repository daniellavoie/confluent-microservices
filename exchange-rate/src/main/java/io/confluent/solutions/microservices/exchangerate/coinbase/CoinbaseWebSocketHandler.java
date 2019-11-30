package io.confluent.solutions.microservices.exchangerate.coinbase;

import java.io.IOException;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.confluent.solutions.microservices.exchangerate.coinbase.model.CoinbaseChannelName;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.CoinbaseMessage;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.CoinbaseMessageType;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBookEvent;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.ProductId;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

public class CoinbaseWebSocketHandler implements WebSocketHandler {

	private final ProductId[] productsIds;
	private final FluxSink<OrderBookEvent> sink;
	
	private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

	public CoinbaseWebSocketHandler(ProductId[] productsIds, FluxSink<OrderBookEvent> sink) {
		this.productsIds = productsIds;
		this.sink = sink;
	}

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		// Step 1 - Send a subscribe message
		return sendSubscribeMessage(session, productsIds)

				// Step 2 - Subscribe to the 
				.thenMany(session.receive())

				// Step 3 - Convert notification to text
				.map(this::convertMessageToText)

				// Step 4 - Convert unmarshall payload 
				.map(value -> readMessage(value, OrderBookEvent.class))

				// Step 5 - Forward notifications
				.doOnNext(sink::next)

				// Step 6 - Forward error
				.doOnError(sink::error)

				// Step 7 - Forward completion signal
				.doOnComplete(sink::complete)

				.then();
	}

	private String convertMessageToText(WebSocketMessage message) {
		return message.getPayloadAsText();
	}

	private WebSocketMessage generateSubscribeMessage(WebSocketSession session) {
		return session.textMessage(writeMessage(new CoinbaseMessage(CoinbaseMessageType.SUBSCRIBE, productsIds,
				new CoinbaseChannelName[] { CoinbaseChannelName.LEVEL_2 })));
	}

	private Mono<Void> sendSubscribeMessage(WebSocketSession session, ProductId[] productsIds) {
		return session.send(Mono.just(generateSubscribeMessage(session)));
	}

	private <T> T readMessage(String value, Class<T> valueType) {
		try {
			return objectMapper.readValue(value, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String writeMessage(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
