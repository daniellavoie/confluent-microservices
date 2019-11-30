package io.confluent.solutions.microservices.exchangerate.coinbase.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.confluent.solutions.microservices.exchangerate.coinbase.OrderBookEventService;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBookEvent;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.ProductId;
import reactor.core.publisher.Flux;

public class MockedOrderBookEventService implements OrderBookEventService {
	private final boolean repeat;

	private List<OrderBookEvent> events;

	public MockedOrderBookEventService(boolean repeat) throws IOException {
		this.events = loadEvents();
		this.repeat = repeat;
	}

	@Override
	public Flux<OrderBookEvent> getEvents(ProductId[] productsIds) {
		Flux<OrderBookEvent> flux = Flux.fromIterable(events);

		if (repeat) {
			flux = flux.delayElements(Duration.ofMillis(100)).repeat();
		}

		return flux;
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
