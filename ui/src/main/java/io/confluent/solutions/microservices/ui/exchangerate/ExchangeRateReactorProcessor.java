package io.confluent.solutions.microservices.ui.exchangerate;

import io.confluent.solutions.microservices.ui.model.ExchangeRateDTO;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

public class ExchangeRateReactorProcessor implements ExchangeRateProcessor {
	DirectProcessor<ExchangeRateDTO> processor = DirectProcessor.create();
	Flux<ExchangeRateDTO> exchangeRateFlux = processor.onBackpressureBuffer().onBackpressureBuffer(1,
			BufferOverflowStrategy.DROP_OLDEST);

	@Override
	public Flux<ExchangeRateDTO> getExchangeRates() {
		return exchangeRateFlux;
	}

	@Override
	public void updateExchangeRate(String productId, ExchangeRateDTO exchangeRate) {
		processor.onNext(exchangeRate);
	}
}
