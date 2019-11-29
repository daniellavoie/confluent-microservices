package io.confluent.solutions.microservices.transaction.stream;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import io.confluent.solutions.microservices.currencyspotter.exchangerate.ExchangeRate;

public class ExchangeRateProcessor implements Processor<String, ExchangeRate> {
	private final String rateStateStoreName;

	private KeyValueStore<String, ExchangeRate> rateStore;

	public ExchangeRateProcessor(String rateStateStoreName) {
		this.rateStateStoreName = rateStateStoreName;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		rateStore = (KeyValueStore<String, ExchangeRate>) context.getStateStore(rateStateStoreName);
	}

	@Override
	public void process(String key, ExchangeRate value) {
		rateStore.put(key, value);
	}

	@Override
	public void close() {

	}
}
