package io.confluent.solutions.microservices.transaction.store;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("transaction.stores.account-currency-balance")
public class AccountCurrencyStoreConfiguration {
	private String name = "account-currency-balance";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
