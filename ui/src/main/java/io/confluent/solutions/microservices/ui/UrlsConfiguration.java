package io.confluent.solutions.microservices.ui;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("urls")
public class UrlsConfiguration {
	private String spotter;
	private String operation;
	private String wallet;

	public String getSpotter() {
		return spotter;
	}

	public void setSpotter(String spotter) {
		this.spotter = spotter;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getWallet() {
		return wallet;
	}

	public void setWallet(String wallet) {
		this.wallet = wallet;
	}
}
