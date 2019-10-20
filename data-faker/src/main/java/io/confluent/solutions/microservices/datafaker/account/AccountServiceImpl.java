package io.confluent.solutions.microservices.datafaker.account;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class AccountServiceImpl implements AccountService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

	private AccountRepository accountRepository;
	private int startingAccountNumber;
	private int numbersOfAccount;

	public AccountServiceImpl(AccountRepository accountRepository,
			@Value("${datafaker.account.starting-account-number:1}") int startingAccountNumber,
			@Value("${datafaker.account.numbers:10000000}") int numbersOfAccount) {
		this.accountRepository = accountRepository;

		this.startingAccountNumber = startingAccountNumber;
		this.numbersOfAccount = numbersOfAccount;
	}

	@Override
	public void generateAccounts() {
		Faker faker = new Faker();

		int lastAccountNumber = startingAccountNumber + numbersOfAccount;

		int currentAccountNumber = Optional
				.of(accountRepository.findByNumberGreaterThanOrderByNumberAsc(startingAccountNumber,
						PageRequest.of(0, 1)))

				.map(page -> page.getNumberOfElements() > 0 ? page.getContent().get(0) : null)

				.map(account -> account.getNumber() + 1).orElse(startingAccountNumber);

		
		
		if (currentAccountNumber < lastAccountNumber) {
			Flux.range(currentAccountNumber, lastAccountNumber)

					.map(index -> new Account(index, faker.name().firstName(), faker.name().lastName(),
							faker.address().streetName(), faker.address().buildingNumber(), faker.address().city(),
							faker.address().country()))

					.buffer(10000)

					.doOnNext(accountRepository::saveAll)

					.doOnNext(accounts -> LOGGER.info("Generated account number {}",
							accounts.get(accounts.size() - 1).getNumber()))

					.subscribeOn(Schedulers.newSingle("Account-Generator"))

					.subscribe();
		}
	}

}
