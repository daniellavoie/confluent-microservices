package io.confluent.solutions.microservices.currencyspotter.coinbase.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.confluent.solutions.microservices.currencyspotter.coinbase.OrderBookEventService;
import io.confluent.solutions.microservices.currencyspotter.coinbase.model.OrderBookEvent;
import io.confluent.solutions.microservices.currencyspotter.coinbase.model.ProductId;
import reactor.core.publisher.Flux;

public class MockedOrderBookEventService implements OrderBookEventService {
	private List<OrderBookEvent> events;

	public MockedOrderBookEventService() throws IOException {
		this.events = loadEvents();
	}

	@Override
	public Flux<OrderBookEvent> getEvents(ProductId[] productsIds) {
		return Flux.fromIterable(events);
	}

	private List<OrderBookEvent> loadEvents() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

		try (BufferedReader buffer = new BufferedReader(
				new InputStreamReader(new ClassPathResource("mock/websocket.data").getInputStream()))) {
			return buffer.lines().map(line -> readValue(line, objectMapper)).collect(Collectors.toList());
		}
	}

	private OrderBookEvent readValue(String value, ObjectMapper objectMapper) {
		try {
			return objectMapper.readValue(value, OrderBookEvent.class);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
