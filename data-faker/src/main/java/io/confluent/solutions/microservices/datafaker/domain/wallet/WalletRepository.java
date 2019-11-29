package io.confluent.solutions.microservices.datafaker.domain.wallet;

import reactor.core.publisher.Flux;

public interface WalletRepository {
	Flux<Wallet> findAll();
}
