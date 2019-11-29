package io.confluent.solutions.microservices.wallet.topic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("wallet.topics.account")
public class AccountConfiguration extends TopicConfiguration {

}
