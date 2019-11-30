package io.confluent.solutions.microservices.wallet.it;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("wallet.topics.wallet")
public class WalletConfiguration extends TopicConfiguration {

}
