package io.confluent.solutions.microservices.datafaker.domain.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
	Page<Account> findByNumberBetweenOrderByNumberDesc(int from, int to, Pageable pageable);
}
