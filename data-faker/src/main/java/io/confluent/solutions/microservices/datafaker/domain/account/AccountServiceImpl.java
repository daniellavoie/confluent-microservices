package io.confluent.solutions.microservices.datafaker.domain.account;

import java.time.Duration;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class AccountServiceImpl implements AccountService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

	private AccountRepository accountRepository;
	private int startingAccountNumber;
	private int lastAccountNumber;

	public AccountServiceImpl(AccountRepository accountRepository,
			@Value("${datafaker.account.starting-account-number:1}") int startingAccountNumber,
			@Value("${datafaker.account.numbers:1000}") int numbersOfAccount) {
		this.accountRepository = accountRepository;

		this.startingAccountNumber = startingAccountNumber;
		this.lastAccountNumber = startingAccountNumber + numbersOfAccount;
	}

	@Override
	public void generateAccounts() {
		Faker faker = new Faker();

		Mono.<Page<Account>>create(
				sink -> sink.success(accountRepository.findByNumberBetweenOrderByNumberDesc(startingAccountNumber - 1,
						lastAccountNumber + 1, PageRequest.of(0, 1))))

				.map(page -> page.getContent().size() == 0 ? startingAccountNumber
						: page.getContent().get(0).getNumber())

				.filter(latestAccount -> latestAccount < lastAccountNumber)

				.flatMapMany(latestAccount -> Flux.range(latestAccount + 1, lastAccountNumber))

				.map(index -> new Account(index, faker.name().firstName(), faker.name().lastName(),
						faker.address().streetName(), faker.address().buildingNumber(), faker.address().city(),
						faker.address().country(), LocalDateTime.now(), LocalDateTime.now()))

				.buffer(10000)

				.doOnNext(accountRepository::saveAll)

				.doOnNext(accounts -> LOGGER.info("Generated account number {} and injected funds.",
						accounts.get(accounts.size() - 1).getNumber()))

				.retryBackoff(10, Duration.ofSeconds(1))

				.doOnError(ex -> LOGGER.error("Failed to rety", ex))

				.subscribeOn(Schedulers.newSingle("Account-Generator"))

				.subscribe();
	}
}
