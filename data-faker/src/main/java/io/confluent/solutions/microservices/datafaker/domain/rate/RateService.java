package io.confluent.solutions.microservices.datafaker.domain.rate;

import java.util.List;

import reactor.core.publisher.Mono;

public interface RateService {
	Mono<List<String>> findExchangeableCurrencies(String currency);
}
