package io.confluent.solutions.microservices.datafaker.domain.rate;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class RateServiceImpl implements RateService {
	@Override
	public Mono<List<String>> findExchangeableCurrencies(String currency) {
		if ("USD".equals(currency)) {
			return Mono.just(Arrays.asList("BTC", "ETH"));
		} else if ("EUR".equals(currency)) {
			return Mono.just(Arrays.asList("ETH"));
		} else if ("BTC".equals(currency)) {
			return Mono.just(Arrays.asList("USD"));
		} else {
			return Mono.error(() -> new RuntimeException("Could not find any exchangeable currency for " + currency));
		}
	}

}
