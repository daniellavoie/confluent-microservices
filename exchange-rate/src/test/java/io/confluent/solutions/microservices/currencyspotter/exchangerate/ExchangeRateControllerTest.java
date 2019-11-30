package io.confluent.solutions.microservices.currencyspotter.exchangerate;

import java.time.Duration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import io.confluent.solutions.microservices.exchangerate.ExchangeRateApplication;
import io.confluent.solutions.microservices.exchangerate.ExchangeRateProcessor;
import reactor.test.StepVerifier;

@SpringBootTest(classes = ExchangeRateApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = "coinbase.mock.repeat=true")
@RunWith(SpringRunner.class)
public class ExchangeRateControllerTest {
	@LocalServerPort
	private int port;

	@Autowired
	ExchangeRateProcessor processor;

	@Test
	public void assertExchangeRateStream() {
		WebClient webClient = WebClient.builder().baseUrl("http://localhost:" + port).build();

		StepVerifier

				.create(webClient.get().uri("/api/exchange-rate").retrieve()

						.bodyToFlux(String.class).take(10))

				// Asserts 10 events were notified.
				.expectNextCount(10)

				// Asserts that the stream received a complete signal.
				.expectComplete()

				// Trigger the assertions.
				.verify(Duration.ofSeconds(10));
	}
}
