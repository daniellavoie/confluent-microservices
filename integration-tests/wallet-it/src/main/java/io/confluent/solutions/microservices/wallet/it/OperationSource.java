package io.confluent.solutions.microservices.wallet.it;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OperationSource {
	String CHANNEL = "operation";

	@Output(OperationSource.CHANNEL)
	MessageChannel output();
}
