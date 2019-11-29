package io.confluent.solutions.microservices.transaction.store;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("transaction.stores.rate")
public class RateStoreConfiguration {
	private String name = "rate";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
