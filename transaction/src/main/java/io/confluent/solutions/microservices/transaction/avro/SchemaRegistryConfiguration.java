package io.confluent.solutions.microservices.transaction.avro;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("transaction.schema-registry")
public class SchemaRegistryConfiguration {
	private String url;
	private String key;
	private String secret;
	
	@Value("${transaction.schema-registry.basic.auth.credentials.source}")
	private String basicAuthCredentialsSource;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getBasicAuthCredentialsSource() {
		return basicAuthCredentialsSource;
	}

	public void setBasicAuthCredentialsSource(String basicAuthCredentialsSource) {
		this.basicAuthCredentialsSource = basicAuthCredentialsSource;
	}
}
