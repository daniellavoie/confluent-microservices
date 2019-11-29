package io.confluent.solutions.microservices.transaction.topic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("transaction.topics.transaction-failed")
public class TransactionFailedConfiguration extends TopicConfiguration {

}
