package io.confluent.solutions.microservices.datafaker.domain.transaction;

import io.confluent.solutions.microservices.datafaker.domain.wallet.Wallet;
import reactor.core.publisher.Mono;

public interface TransactionService {
	Mono<Transaction> generateRandomTransaction(Wallet wallet);
}
