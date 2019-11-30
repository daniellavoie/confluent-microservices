package io.confluent.solutions.microservices.exchangerate.coinbase;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "coinbase")
public class CoinbaseConfiguration {
	private String websocketUrl;

	public String getWebsocketUrl() {
		return websocketUrl;
	}

	public void setWebsocketUrl(String websocketUrl) {
		this.websocketUrl = websocketUrl;
	}
}