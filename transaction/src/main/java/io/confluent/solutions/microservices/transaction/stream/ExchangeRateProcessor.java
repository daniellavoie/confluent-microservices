package io.confluent.solutions.microservices.transaction.stream;

import java.util.Optional;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.confluent.solutions.microservices.exchangerate.ExchangeRate;

public class ExchangeRateProcessor implements Processor<String, ExchangeRate> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateProcessor.class);

	private final String rateStateStoreName;

	private KeyValueStore<String, ExchangeRate> rateStore;

	public ExchangeRateProcessor(String rateStateStoreName) {
		LOGGER.trace("Creating a new rate processor.");

		this.rateStateStoreName = rateStateStoreName;
	}

	@Override
	public void close() {

	}

	public Optional<ExchangeRate> get(String key) {
		LOGGER.trace("Retreiving exchange rate {} from rate state store {}.", key, this);

		return Optional.ofNullable(rateStore.get(key));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		LOGGER.trace("Initializing rate state store {} named {}.", this, rateStateStoreName);

		rateStore = (KeyValueStore<String, ExchangeRate>) context.getStateStore(rateStateStoreName);

		LOGGER.trace("Retreived rate state store {}.", rateStore);
	}

	@Override
	public void process(String key, ExchangeRate value) {
		LOGGER.trace("Storing {} in rate state store {} with key {}.", value, this, key);

		rateStore.put(key, value);
	}

}
