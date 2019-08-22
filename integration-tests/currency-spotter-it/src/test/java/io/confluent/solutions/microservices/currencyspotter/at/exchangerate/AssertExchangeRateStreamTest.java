package io.confluent.solutions.microservices.currencyspotter.at.exchangerate;

import java.time.Duration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.confluent.solutions.microservices.currencyspotter.exchangerate.ExchangeRate;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CurrencySpotterAcceptanceApplication.class)
public class AssertExchangeRateStreamTest {
	@Autowired
	private ExchangeRateListener listener;

	@Test
	public void assertExchangeRateStream() {
		StepVerifier

				.create(Flux.<ExchangeRate>create(listener::setSink).take(10))

				// Asserts 10 events were notified.
				.expectNextCount(10)

				// Asserts that the stream received a complete signal.
				.expectComplete()

				// Trigger the assertions.
				.verify(Duration.ofSeconds(10));
	}
}
