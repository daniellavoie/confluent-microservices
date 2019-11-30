package io.confluent.solutions.microservices.exchangerate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

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
	public Flux<ExchangeRateResponse> getExchangeRates() {
		return exchangeRateProcessor.getExchangeRates().map(this::map);
	}

	private ExchangeRateResponse map(ExchangeRate exchangeRate) {
		return new ExchangeRateResponse(exchangeRate.getBaseCurrency().toString(),
				exchangeRate.getQuoteCurrency().toString(), exchangeRate.getAsk(), exchangeRate.getBid(),
				LocalDateTime.ofInstant(Instant.ofEpochMilli(exchangeRate.getTimestamp()),
						TimeZone.getDefault().toZoneId()));
	}
}