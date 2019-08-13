package io.confluent.solutions.microservices.wallet.it;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding(OperationSource.class)
public class WalletIntegrationTestsApplication {

}
