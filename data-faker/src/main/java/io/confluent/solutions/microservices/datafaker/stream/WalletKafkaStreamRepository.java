package io.confluent.solutions.microservices.datafaker.stream;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.confluent.solutions.microservices.datafaker.domain.wallet.Wallet;
import io.confluent.solutions.microservices.datafaker.domain.wallet.WalletRepository;
import reactor.core.publisher.Flux;

public class WalletKafkaStreamRepository implements WalletRepository, Processor<String, Wallet> {
	private static final Logger LOGGER = LoggerFactory.getLogger(WalletKafkaStreamRepository.class);

	private final String walletStoreName;

	private KeyValueStore<String, Wallet> walletStore;

	public WalletKafkaStreamRepository(String walletStoreName) {
		this.walletStoreName = walletStoreName;
	}

	@Override
	public Flux<Wallet> findAll() {
		LOGGER.trace("Fetching all wallet from store.");

		return walletStore == null ? Flux.empty()
				: Flux.fromIterable(() -> walletStore.all()).map(keyValue -> keyValue.value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(ProcessorContext context) {
		walletStore = (KeyValueStore<String, Wallet>) context.getStateStore(walletStoreName);
	}

	@Override
	public void process(String key, Wallet value) {
		LOGGER.trace("Updating wallet store with {}.", value);

		walletStore.put(key, value);
	}

	@Override
	public void close() {

	}

}
