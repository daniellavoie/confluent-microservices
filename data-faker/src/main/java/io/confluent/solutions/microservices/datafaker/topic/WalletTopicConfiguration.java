package io.confluent.solutions.microservices.datafaker.topic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("data-faker.topics.wallet")
public class WalletTopicConfiguration extends TopicConfiguration {

}
