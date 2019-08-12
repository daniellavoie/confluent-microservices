package io.confluent.solutions.microservices.currencyspotter.exchangerate;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface ExchangeRateSink {
	String CHANNEL = "exchange-rate";

	@Output(ExchangeRateSink.CHANNEL)
	MessageChannel output();
}
