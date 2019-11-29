package io.confluent.solutions.microservices.datafaker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import io.confluent.solutions.microservices.datafaker.domain.wallet.Wallet;
import io.confluent.solutions.microservices.datafaker.store.WalletStoreConfiguration;
import io.confluent.solutions.microservices.datafaker.stream.WalletKafkaStreamRepository;
import io.confluent.solutions.microservices.datafaker.stream.WalletStream;
import io.confluent.solutions.microservices.datafaker.topic.TopicConfiguration;
import io.confluent.solutions.microservices.datafaker.topic.WalletTopicConfiguration;

@Configuration
@EnableKafkaStreams
public class KafkaStreamConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamConfig.class);

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
		createTopicIfMissing(walletTopicConfiguration, adminClient);

		streamsBuilder.addStateStore(
				Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(walletStoreConfiguration.getName()),
						Serdes.String(), new JsonSerde<Wallet>(Wallet.class)));

		WalletStream.build(walletTopicConfiguration.getName(), walletKafkaStreamRepository,
				walletStoreConfiguration.getName(), streamsBuilder);

		return streamsBuilder.build();
	}

	private void createTopicIfMissing(TopicConfiguration topicConfiguration, AdminClient adminClient) {
		try {
			if (!adminClient.listTopics().names().get().stream()
					.filter(existingTopic -> existingTopic.equals(topicConfiguration.getName())).findAny()
					.isPresent()) {
				LOGGER.info("Creating topic {}.", topicConfiguration.getName());

				NewTopic topic = new NewTopic(topicConfiguration.getName(), topicConfiguration.getPartitions(),
						topicConfiguration.getReplicationFactor());

				topic.configs(new HashMap<>());
				topic.configs().put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);

				adminClient.createTopics(Arrays.asList(topic)).all().get();
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
