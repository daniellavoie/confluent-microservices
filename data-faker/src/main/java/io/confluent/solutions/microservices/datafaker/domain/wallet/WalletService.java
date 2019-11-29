package io.confluent.solutions.microservices.datafaker.domain.wallet;

import reactor.core.publisher.Mono;

public interface WalletService {
	Mono<Void> generateTransactions();
}
