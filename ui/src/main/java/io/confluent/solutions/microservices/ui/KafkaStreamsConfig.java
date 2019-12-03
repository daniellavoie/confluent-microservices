package io.confluent.solutions.microservices.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.confluent.solutions.microservices.ui.exchangerate.ExchangeRateProcessor;
import io.confluent.solutions.microservices.ui.exchangerate.ExchangeRateStream;
import io.confluent.solutions.microservices.ui.topic.ExchangeRateConfiguration;
import io.confluent.solutions.microservices.ui.topic.TopicConfiguration;

@Configuration
public class KafkaStreamsConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsConfig.class);

	@Bean
	public AdminClient adminClient(KafkaProperties kafkaProperties) {
		return AdminClient.create(kafkaProperties.buildAdminProperties());
	}

	@Bean
	public Topology topology(ExchangeRateProcessor exchangeRateProcessor,
			ExchangeRateConfiguration exchangeRateConfiguration, AdminClient adminClient,
			StreamsBuilder streamsBuilder) {
		createTopicIfMissing(exchangeRateConfiguration, adminClient);

		ExchangeRateStream.exchangeRateProcessorStream(exchangeRateProcessor,
				streamsBuilder.stream(exchangeRateConfiguration.getName()));

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
