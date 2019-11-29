package io.confluent.solutions.microservices.datafaker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.confluent.solutions.microservices.datafaker.domain.account.AccountService;
import io.confluent.solutions.microservices.datafaker.domain.wallet.WalletService;

@SpringBootApplication
public class DataFakerApplication implements CommandLineRunner {
	private AccountService accountService;
	private WalletService walletService;

	public DataFakerApplication(AccountService accountService, WalletService walletService) {
		this.accountService = accountService;
		this.walletService = walletService;
	}

	public static void main(String[] args) {
		SpringApplication.run(DataFakerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		accountService.generateAccounts();

		do {
			walletService.generateTransactions().block();

			Thread.sleep(500);
		} while (true);
	}

}
