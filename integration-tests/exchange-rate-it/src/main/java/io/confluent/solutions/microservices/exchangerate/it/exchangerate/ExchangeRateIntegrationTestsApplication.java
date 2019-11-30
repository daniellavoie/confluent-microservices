package io.confluent.solutions.microservices.exchangerate.it.exchangerate;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding(ExchangeRateSink.class)
public class ExchangeRateIntegrationTestsApplication {
	
}