package io.confluent.solutions.microservices.exchangerate.coinbase;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import io.confluent.solutions.microservices.exchangerate.ExchangeRate;
import io.confluent.solutions.microservices.exchangerate.ExchangeRateApplication;
import io.confluent.solutions.microservices.exchangerate.coinbase.CoinbaseOrderBookEventService;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBookEvent;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.ProductId;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(classes = ExchangeRateApplication.class, properties = "coinbase.mock.enabled=false")
public class AssertCoinbaseOrderBookEvents {
	@Autowired
	private CoinbaseOrderBookEventService coinbaseOrderBookEventService;
	
	@MockBean
	private KafkaTemplate<String, ExchangeRate> exchangeRateTemplate;

	@Test
	public void fetch10events() {

		// Retreive the order book event stream for the BTC_USD product.
		Flux<OrderBookEvent> orderBookEventStream = coinbaseOrderBookEventService
				.getEvents(new ProductId[] { ProductId.BTC_USD })

				// Send a completion signal after 10 notifications.
				.take(10);

		StepVerifier.create(orderBookEventStream)

				// Asserts 10 events were notified.
				.expectNextCount(10)

				// Asserts that the stream received a complete signal.
				.expectComplete()

				// Trigger the assertions.
				.verify(Duration.ofSeconds(5));
	}
}