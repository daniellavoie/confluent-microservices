package io.confluent.solutions.microservices.datafaker.stream;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;

import io.confluent.solutions.microservices.datafaker.domain.wallet.Wallet;

public class WalletStream {
	public static KStream<String, Wallet> build(String walletTopicName, WalletKafkaStreamRepository walletKafkaStreamRepository,
			String walletStoreName, StreamsBuilder streamsBuilder) {
		KStream<String, Wallet> stream = streamsBuilder.stream(walletTopicName);

		stream.process(() -> walletKafkaStreamRepository, walletStoreName);

		return stream;
	}
}
