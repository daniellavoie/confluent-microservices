package io.confluent.solutions.microservices.transaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.confluent.solutions.microservices.exchangerate.ExchangeRate;
import io.confluent.solutions.microservices.transaction.model.AccountCurrencyBalance;
import io.confluent.solutions.microservices.transaction.model.OperationResult;
import io.confluent.solutions.microservices.transaction.model.Transaction;
import io.confluent.solutions.microservices.transaction.store.AccountCurrencyStoreConfiguration;
import io.confluent.solutions.microservices.transaction.store.RateStoreConfiguration;
import io.confluent.solutions.microservices.transaction.stream.ExchangeRateProcessor;
import io.confluent.solutions.microservices.transaction.stream.OperationCreditStream;
import io.confluent.solutions.microservices.transaction.stream.OperationDebitStream;
import io.confluent.solutions.microservices.transaction.stream.TransactionSuccessStream;
import io.confluent.solutions.microservices.transaction.topic.ExchangeRateConfiguration;
import io.confluent.solutions.microservices.transaction.topic.TopicConfiguration;
import io.confluent.solutions.microservices.transaction.topic.TransactionFailedConfiguration;
import io.confluent.solutions.microservices.transaction.topic.TransactionRequestConfiguration;
import io.confluent.solutions.microservices.transaction.topic.TransactionSuccessConfiguration;

@Configuration
@ConditionalOnProperty(name = "transaction.streams.enabled", matchIfMissing = true)
public class KafkaStreamConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamConfig.class);

	@Bean
	public AdminClient adminClient(KafkaProperties kafkaProperties) {
		return AdminClient.create(kafkaProperties.buildAdminProperties());
	}

	@Bean
	public SpecificAvroSerde<ExchangeRate> exchangeRateValueSerde(KafkaProperties kafkaProperties) {
		SpecificAvroSerde<ExchangeRate> serde = new SpecificAvroSerde<ExchangeRate>();

		serde.configure(kafkaProperties.buildStreamsProperties(), false);

		return serde;
	}

	@Bean
	public Topology topology(AdminClient adminClient, StreamsBuilder streamsBuilder,
			ExchangeRateConfiguration exchangeRateConfiguration,
			TransactionFailedConfiguration transactionFailedConfiguration,
			TransactionRequestConfiguration transactionRequestConfiguration,
			TransactionSuccessConfiguration transactionSuccessConfiguration,
			AccountCurrencyStoreConfiguration accountCurrencyStoreConfiguration,
			RateStoreConfiguration rateStoreConfiguration, SpecificAvroSerde<ExchangeRate> exchangeRateValueSerde) {
		createTopicIfMissing(transactionRequestConfiguration, adminClient);
		createTopicIfMissing(transactionFailedConfiguration, adminClient);
		createTopicIfMissing(transactionSuccessConfiguration, adminClient);
		createTopicIfMissing(exchangeRateConfiguration, adminClient);

		streamsBuilder.addStateStore(
				Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(accountCurrencyStoreConfiguration.getName()),
						Serdes.String(), new JsonSerde<AccountCurrencyBalance>(AccountCurrencyBalance.class)));

		streamsBuilder.addGlobalStore(
				Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(rateStoreConfiguration.getName()),
						Serdes.String(), exchangeRateValueSerde),
				exchangeRateConfiguration.getName(), Consumed.with(Serdes.String(), exchangeRateValueSerde),
				() -> new ExchangeRateProcessor(rateStoreConfiguration.getName()));

		KStream<String, Transaction> transactionRequestStream = streamsBuilder
				.stream(transactionRequestConfiguration.getName());

		KStream<String, OperationResult> operationDebitStream = OperationDebitStream.build(transactionRequestStream,
				accountCurrencyStoreConfiguration.getName());

		KStream<String, OperationResult> operationCreditStream = OperationCreditStream.build(transactionRequestStream,
				operationDebitStream, accountCurrencyStoreConfiguration.getName(), rateStoreConfiguration.getName());

		TransactionSuccessStream.build(operationCreditStream, operationDebitStream, transactionSuccessConfiguration);

		Topology topology = streamsBuilder.build();

		LOGGER.trace("Topology description : {}", topology.describe());

		return topology;
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
