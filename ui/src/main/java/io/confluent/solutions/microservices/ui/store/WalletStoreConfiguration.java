package io.confluent.solutions.microservices.ui.store;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ui.stores.wallet")
public class WalletStoreConfiguration {
	private String name = "wallet";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
