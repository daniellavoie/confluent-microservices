package io.confluent.solutions.microservices.datafaker;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.Stores;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import io.confluent.solutions.microservices.datafaker.domain.wallet.Wallet;
import io.confluent.solutions.microservices.datafaker.store.WalletStoreConfiguration;
import io.confluent.solutions.microservices.datafaker.stream.WalletKafkaStreamRepository;
import io.confluent.solutions.microservices.datafaker.stream.WalletStream;
import io.confluent.solutions.microservices.datafaker.topic.WalletTopicConfiguration;

@Configuration
@EnableKafkaStreams
public class KafkaStreamConfig {
	@Bean
	public AdminClient adminClient(KafkaProperties kafkaProperties) {
		return AdminClient.create(kafkaProperties.buildAdminProperties());
	}

	@Bean
	public WalletKafkaStreamRepository walletKafkaStreamRepository(WalletStoreConfiguration walletStoreConfiguration) {
		return new WalletKafkaStreamRepository(walletStoreConfiguration.getName());
	}

	@Bean
	public Topology toplogy(WalletTopicConfiguration walletTopicConfiguration,
			WalletStoreConfiguration walletStoreConfiguration, WalletKafkaStreamRepository walletKafkaStreamRepository,
			StreamsBuilder streamsBuilder, AdminClient adminClient) {
		KafkaUtil.createTopicIfMissing(walletTopicConfiguration, adminClient);

		streamsBuilder.addStateStore(
				Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(walletStoreConfiguration.getName()),
						Serdes.String(), new JsonSerde<Wallet>(Wallet.class)));

		WalletStream.build(walletTopicConfiguration.getName(), walletKafkaStreamRepository,
				walletStoreConfiguration.getName(), streamsBuilder);

		return streamsBuilder.build();
	}
}
