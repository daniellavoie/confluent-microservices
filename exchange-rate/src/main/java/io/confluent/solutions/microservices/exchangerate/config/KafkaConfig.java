package io.confluent.solutions.microservices.exchangerate.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import io.confluent.solutions.microservices.exchangerate.ExchangeRate;

@Configuration
public class KafkaConfig {
	
	@Bean
	public KafkaTemplate<String, ExchangeRate> exchangeRateTemplate(KafkaProperties kafkaProperties) {
		return new KafkaTemplate<String, ExchangeRate>(
				new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()));
	}
}
