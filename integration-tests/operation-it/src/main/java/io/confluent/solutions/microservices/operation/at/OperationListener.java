package io.confluent.solutions.microservices.operation.at;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
public class OperationListener {
	private List<Operation> operations = new ArrayList<>();
	private CountDownLatch latch = new CountDownLatch(1);

	@StreamListener(OperationSink.CHANNEL)
	public void handle(Operation operation) {
		operations.add(operation);
		latch.countDown();
	}

	public List<Operation> getOperations() {
		try {
			if (!latch.await(20, TimeUnit.SECONDS)) {
				throw new RuntimeException("Timeout");
			}
			return operations;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
