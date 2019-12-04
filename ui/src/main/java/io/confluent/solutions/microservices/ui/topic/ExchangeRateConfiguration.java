package io.confluent.solutions.microservices.ui.topic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ui.topics.exchange-rate")
public class ExchangeRateConfiguration extends TopicConfiguration {

}
