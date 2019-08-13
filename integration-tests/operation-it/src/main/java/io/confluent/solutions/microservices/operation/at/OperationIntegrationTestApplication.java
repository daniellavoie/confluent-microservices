package io.confluent.solutions.microservices.operation.at;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding(OperationSink.class)
public class OperationIntegrationTestApplication {

}
