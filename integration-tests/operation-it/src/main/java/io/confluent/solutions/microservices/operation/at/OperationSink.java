package io.confluent.solutions.microservices.operation.at;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface OperationSink {
	String CHANNEL = "operation";

	@Input(OperationSink.CHANNEL)
	SubscribableChannel input();
}