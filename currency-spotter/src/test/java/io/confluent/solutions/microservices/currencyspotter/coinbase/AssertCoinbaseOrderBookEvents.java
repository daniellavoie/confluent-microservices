package io.confluent.solutions.microservices.currencyspotter.coinbase;

import java.time.Duration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.confluent.solutions.microservices.currencyspotter.coinbase.model.OrderBookEvent;
import io.confluent.solutions.microservices.currencyspotter.coinbase.model.ProductId;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "coinbase.mock.enabled=false")
public class AssertCoinbaseOrderBookEvents {
	@Autowired
	private CoinbaseOrderBookEventService coinbaseOrderBookEventService;

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