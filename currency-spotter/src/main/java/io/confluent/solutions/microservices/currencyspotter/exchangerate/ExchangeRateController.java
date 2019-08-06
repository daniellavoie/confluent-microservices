package io.confluent.solutions.microservices.currencyspotter.exchangerate;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {
	private ExchangeRateProcessor exchangeRateProcessor;

	public ExchangeRateController(ExchangeRateProcessor exchangeRateProcessor) {
		this.exchangeRateProcessor = exchangeRateProcessor;
	}

	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ExchangeRate> getExchangeRates() {
		return exchangeRateProcessor.getExchangeRates();
	}
}