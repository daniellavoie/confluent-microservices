package io.confluent.solutions.microservices.currencyspotter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

import io.confluent.solutions.microservices.currencyspotter.exchangerate.ExchangeRateSink;

@SpringBootApplication
@EnableBinding(ExchangeRateSink.class)
public class CurrencySpotterApplication {
	public static void main(String[] args) {
		SpringApplication.run(CurrencySpotterApplication.class, args);
	}
}
