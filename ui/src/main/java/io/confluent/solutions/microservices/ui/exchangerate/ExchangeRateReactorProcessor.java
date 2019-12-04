package io.confluent.solutions.microservices.ui.exchangerate;

import io.confluent.solutions.microservices.ui.model.ExchangeRateDTO;
import reactor.core.publisher.Flux;

public interface ExchangeRateProcessor {
	Flux<ExchangeRateDTO> getExchangeRates();

	void updateExchangeRate(String productId, ExchangeRateDTO exchangeRate);
}
