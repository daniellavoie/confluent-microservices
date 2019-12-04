package io.confluent.solutions.microservices.ui.exchangerate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.confluent.solutions.microservices.exchangerate.ExchangeRate;
import io.confluent.solutions.microservices.ui.model.ExchangeRateDTO;
import io.confluent.solutions.microservices.ui.store.ExchangeRateStoreConfiguration;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

@Service
public class ExchangeRateReactorProcessor implements ExchangeRateProcessor, Processor<String, ExchangeRate> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateProcessor.class);

	private DirectProcessor<ExchangeRateDTO> exchangeRateProcessor = DirectProcessor.create();
	private Flux<ExchangeRateDTO> exchangeRateflux = exchangeRateProcessor.onBackpressureBuffer(1,
			BufferOverflowStrategy.DROP_OLDEST);

	private final String rateStateStoreName;

	private KeyValueStore<String, ExchangeRate> exchangeRateStore;

	public ExchangeRateReactorProcessor(ExchangeRateStoreConfiguration exchangeRateStoreConfiguration) {
		LOGGER.trace("Creating a new rate processor.");

		this.rateStateStoreName = exchangeRateStoreConfiguration.getName();
	}

	@Override
	public void close() {

	}

	public Flux<ExchangeRateDTO> getExchangeRates() {
		return Flux.fromIterable(() -> exchangeRateStore.all()).map(keyValue -> keyValue.value)
				.map(this::mapExchangeRate)

				.concatWith(exchangeRateflux);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		LOGGER.trace("Initializing rate state store {} named {}.", this, rateStateStoreName);

		exchangeRateStore = (KeyValueStore<String, ExchangeRate>) context.getStateStore(rateStateStoreName);

		LOGGER.trace("Retreived rate state store {}.", exchangeRateStore);
	}

	private ExchangeRateDTO mapExchangeRate(ExchangeRate exchangeRate) {
		return new ExchangeRateDTO(exchangeRate.getBaseCurrency().toString(),
				exchangeRate.getQuoteCurrency().toString(), exchangeRate.getAsk(), exchangeRate.getBid(),
				LocalDateTime.ofInstant(Instant.ofEpochMilli(exchangeRate.getTimestamp()),
						TimeZone.getDefault().toZoneId()));
	}

	@Override
	public void process(String key, ExchangeRate exchangeRate) {
		LOGGER.trace("Storing {} in rate state store {} with key {}.", exchangeRate, this, key);
		ExchangeRateDTO exchangeRateDTO = mapExchangeRate(exchangeRate);

		exchangeRateProcessor.onNext(exchangeRateDTO);

		exchangeRateStore.put(key, exchangeRate);

	}

}
