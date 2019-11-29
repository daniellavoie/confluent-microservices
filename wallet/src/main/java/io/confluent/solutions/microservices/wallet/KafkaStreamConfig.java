package io.confluent.solutions.microservices.wallet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import io.confluent.solutions.microservices.wallet.model.Account;
import io.confluent.solutions.microservices.wallet.model.Transaction;
import io.confluent.solutions.microservices.wallet.model.Wallet;
import io.confluent.solutions.microservices.wallet.store.WalletStoreConfiguration;
import io.confluent.solutions.microservices.wallet.stream.AccountToWalletTransformer;
import io.confluent.solutions.microservices.wallet.stream.TransactionToWalletTransformer;
import io.confluent.solutions.microservices.wallet.topic.AccountConfiguration;
import io.confluent.solutions.microservices.wallet.topic.TopicConfiguration;
import io.confluent.solutions.microservices.wallet.topic.TransactionSuccessConfiguration;
import io.confluent.solutions.microservices.wallet.topic.WalletConfiguration;

@Configuration
public class KafkaStreamConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamConfig.class);

	@Bean
	public AdminClient adminClient(KafkaProperties kafkaProperties) {
		return AdminClient.create(kafkaProperties.buildAdminProperties());
	}

	@Bean
	public Lock walletStoreLock() {
		return new ReentrantLock();
	}

	@Bean
	public TransactionToWalletTransformer transactionToWalletTransformer(
			WalletStoreConfiguration walletStoreConfiguration, Lock walletStoreLock) {
		return new TransactionToWalletTransformer(walletStoreConfiguration.getName(), walletStoreLock, 30);
	}

	@Bean
	public AccountToWalletTransformer accountToWalletTransformer(WalletStoreConfiguration walletStoreConfiguration,
			Lock walletStoreLock) {
		return new AccountToWalletTransformer(walletStoreConfiguration.getName(), walletStoreLock, 30);
	}

	@Bean
	public Topology topology(TransactionSuccessConfiguration transactionSuccessConfiguration,
			AccountConfiguration accountConfiguration, WalletConfiguration walletConfiguration,
			WalletStoreConfiguration walletStoreConfiguration,
			TransactionToWalletTransformer transactionToWalletTransformer,
			AccountToWalletTransformer accountToWalletTransformer, StreamsBuilder streamsBuilder,
			AdminClient adminClient) {
		createTopicIfMissing(accountConfiguration, adminClient);
		createTopicIfMissing(transactionSuccessConfiguration, adminClient);
		createTopicIfMissing(walletConfiguration, adminClient);

		streamsBuilder.addStateStore(
				Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(walletStoreConfiguration.getName()),
						Serdes.String(), new JsonSerde<Wallet>(Wallet.class)));

		KStream<String, Transaction> transactionStream = streamsBuilder
				.stream(transactionSuccessConfiguration.getName());

		KStream<String, Account> accountStream = streamsBuilder.stream(accountConfiguration.getName(),
				Consumed.with(Serdes.String(), new JsonSerde<Account>(Account.class)));

		transactionStream.transformValues(() -> transactionToWalletTransformer, walletStoreConfiguration.getName())

				.merge(accountStream.transformValues(() -> accountToWalletTransformer,
						walletStoreConfiguration.getName()))

				.peek(this::traceWallet)

				.to(walletConfiguration.getName());

		return streamsBuilder.build();
	}

	private void traceWallet(String account, Wallet wallet) {
		LOGGER.trace("Notifying {}", wallet);
	}

	private void createTopicIfMissing(TopicConfiguration topicConfiguration, AdminClient adminClient) {
		try {
			if (!adminClient.listTopics().names().get().stream()
					.filter(existingTopic -> existingTopic.equals(topicConfiguration.getName())).findAny()
					.isPresent()) {
				LOGGER.info("Creating topic {}.", topicConfiguration.getName());

				NewTopic topic = new NewTopic(topicConfiguration.getName(), topicConfiguration.getPartitions(),
						topicConfiguration.getReplicationFactor());

				topic.configs(new HashMap<>());
				topic.configs().put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);

				adminClient.createTopics(Arrays.asList(topic)).all().get();
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
