package io.confluent.solutions.microservices.currencyspotter.exchangerate;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

public interface ExchangeRateSink {
	String CHANNEL = "exchange-rate";

	@Output(ExchangeRateSink.CHANNEL)
	SubscribableChannel input();
}
