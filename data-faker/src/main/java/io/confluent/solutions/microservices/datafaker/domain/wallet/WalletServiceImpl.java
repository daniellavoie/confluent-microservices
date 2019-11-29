package io.confluent.solutions.microservices.datafaker.domain.wallet;

import org.springframework.stereotype.Service;

import io.confluent.solutions.microservices.datafaker.domain.transaction.TransactionService;
import reactor.core.publisher.Mono;

@Service
public class WalletServiceImpl implements WalletService {
	private final TransactionService transactionService;
	private final WalletRepository walletRepository;

	public WalletServiceImpl(TransactionService transactionService, WalletRepository walletRepository) {
		this.transactionService = transactionService;
		this.walletRepository = walletRepository;
	}

	@Override
	public Mono<Void> generateTransactions() {
		return walletRepository.findAll()

				.flatMap(transactionService::generateRandomTransaction).then();
	}
}
