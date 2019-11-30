package io.confluent.solutions.microservices.exchangerate.it.exchangerate;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import io.confluent.solutions.microservices.exchangerate.ExchangeRate;
import reactor.core.publisher.FluxSink;

@Component
public class ExchangeRateListener {
	private FluxSink<ExchangeRate> sink;

	@StreamListener(ExchangeRateSink.CHANNEL)
	public void handle(ExchangeRate exchangeRate) {
		if (sink != null) {
			sink.next(exchangeRate);
		}
	}

	public void setSink(FluxSink<ExchangeRate> sink) {
		this.sink = sink;
	}
}
