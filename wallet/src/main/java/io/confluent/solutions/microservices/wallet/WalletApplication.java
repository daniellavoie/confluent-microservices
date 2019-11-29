package io.confluent.solutions.microservices.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@EnableKafkaStreams
@SpringBootApplication
public class WalletApplication {
	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}
}
