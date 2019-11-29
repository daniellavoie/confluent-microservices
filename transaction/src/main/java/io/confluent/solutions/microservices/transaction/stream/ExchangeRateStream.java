package io.confluent.solutions.microservices.transaction.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.confluent.solutions.microservices.currencyspotter.exchangerate.ExchangeRate;
import io.confluent.solutions.microservices.transaction.topic.ExchangeRateConfiguration;

public abstract class ExchangeRateStream {
	public static KStream<String, ExchangeRate> build(ExchangeRateConfiguration topicConfiguration,
			StreamsBuilder streamsBuilder, String rateStoreName,
			SpecificAvroSerde<ExchangeRate> exchangeRateValueSerde) {

		KStream<String, ExchangeRate> stream = streamsBuilder.stream(topicConfiguration.getName(),
				Consumed.with(Serdes.String(), exchangeRateValueSerde));

		stream.process(() -> new ExchangeRateProcessor(rateStoreName), rateStoreName);

		return stream;
	}
}
