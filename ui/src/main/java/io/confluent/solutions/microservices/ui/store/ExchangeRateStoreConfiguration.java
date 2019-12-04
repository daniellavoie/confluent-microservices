package io.confluent.solutions.microservices.ui.store;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ui.stores.exchange-rate")
public class ExchangeRateStoreConfiguration {
	private String name = "exchange-rate";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
