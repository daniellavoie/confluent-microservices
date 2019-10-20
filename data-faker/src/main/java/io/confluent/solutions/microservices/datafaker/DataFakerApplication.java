package io.confluent.solutions.microservices.datafaker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.confluent.solutions.microservices.datafaker.account.AccountService;

@SpringBootApplication
public class DataFakerApplication implements CommandLineRunner {
	private AccountService accountService;

	public DataFakerApplication(AccountService accountService) {
		this.accountService = accountService;
	}

	public static void main(String[] args) {
		SpringApplication.run(DataFakerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		accountService.generateAccounts();
	}

}
