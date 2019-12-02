package io.confluent.solutions.microservices.datafaker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.confluent.solutions.microservices.datafaker.topic.TopicConfiguration;

public abstract class KafkaUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaUtil.class);

	public static void createTopicIfMissing(TopicConfiguration topicConfiguration, AdminClient adminClient) {
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
