package io.confluent.solutions.microservices.currencyspotter.at.exchangerate;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding(ExchangeRateSink.class)
public class CurrencySpotterAcceptanceApplication {
	
}