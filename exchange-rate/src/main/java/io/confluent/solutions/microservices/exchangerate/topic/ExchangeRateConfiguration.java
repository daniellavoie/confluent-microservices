package io.confluent.solutions.microservices.exchangerate.topic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("exchange-rate.topics.exchange-rate")
public class ExchangeRateConfiguration extends TopicConfiguration {

}
