package io.confluent.solutions.microservices.currencyspotter.coinbase;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.confluent.solutions.microservices.currencyspotter.coinbase.mock.MockedOrderBookEventService;

@Configuration
public class CoinbaseConfig {
	@Bean
	@ConditionalOnProperty(name = "coinbase.mock.enabled", matchIfMissing = true, havingValue = "false")
	public OrderBookEventService orderBookEventService(CoinbaseConfiguration coinbaseConfiguration) {
		return new CoinbaseOrderBookEventService(coinbaseConfiguration);
	}

	@Bean
	@ConditionalOnProperty(name = "coinbase.mock.enabled")
	public MockedOrderBookEventService mockedOrderBookEventService(@Value("${coinbase.mock.repeat:false}") boolean repeat)
			throws IOException {
		return new MockedOrderBookEventService(repeat);
	}
}
