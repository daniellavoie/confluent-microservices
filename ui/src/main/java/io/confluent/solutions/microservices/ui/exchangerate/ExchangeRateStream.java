package io.confluent.solutions.microservices.ui.exchangerate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.apache.kafka.streams.kstream.KStream;

import io.confluent.solutions.microservices.exchangerate.ExchangeRate;
import io.confluent.solutions.microservices.ui.model.ExchangeRateDTO;

public abstract class ExchangeRateStream {
	public static KStream<String, ExchangeRateDTO> exchangeRateProcessorStream(
			ExchangeRateProcessor exchangeRateProcessor, KStream<String, ExchangeRate> exchangeRateStream) {
		return exchangeRateStream.mapValues(ExchangeRateStream::mapExchangeRate)

				.peek(exchangeRateProcessor::updateExchangeRate);
	}

	private static ExchangeRateDTO mapExchangeRate(String productId, ExchangeRate exchangeRate) {
		return new ExchangeRateDTO(exchangeRate.getBaseCurrency().toString(),
				exchangeRate.getQuoteCurrency().toString(), exchangeRate.getAsk(), exchangeRate.getBid(),
				LocalDateTime.ofInstant(Instant.ofEpochMilli(exchangeRate.getTimestamp()),
						TimeZone.getDefault().toZoneId()));
	}
}
