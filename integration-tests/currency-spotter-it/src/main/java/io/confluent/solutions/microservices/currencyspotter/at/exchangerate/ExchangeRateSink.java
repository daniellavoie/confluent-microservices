package io.confluent.solutions.microservices.currencyspotter.at.exchangerate;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ExchangeRateSink {
	String CHANNEL = "exchange-rate";

	@Input(ExchangeRateSink.CHANNEL)
	SubscribableChannel input();
}