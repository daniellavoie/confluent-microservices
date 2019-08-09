package io.confluent.solutions.microservices.currencyspotter.exchangerate;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import io.confluent.solutions.microservices.currencyspotter.coinbase.OrderBookService;
import io.confluent.solutions.microservices.currencyspotter.coinbase.model.OrderBookNotification;
import io.confluent.solutions.microservices.currencyspotter.coinbase.model.ProductId;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Service
public class ExchangeRateProcessor implements DisposableBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateProcessor.class);

	private Disposable subscriber;

	private Map<String, FluxSink<ExchangeRate>> subscribers = new ConcurrentHashMap<>();

	public ExchangeRateProcessor(ExchangeRateSink sink, OrderBookService orderBookService) {
		subscriber = orderBookService.orderBookStream(ProductId.values())

				.map(this::mapExchangeRate)

				.doOnNext(exchangeRate -> subscribers.values().forEach(subscriber -> subscriber.next(exchangeRate)))

				.map(this::mapExchangeRate)

				.doOnNext(message -> sink.input().send(message))

				.doOnError(ex -> LOGGER.error("Error while processing order book events", ex))

				.retryBackoff(Long.MAX_VALUE, Duration.ofMillis(100), Duration.ofMinutes(1))

				.subscribe();
	}

	public Flux<ExchangeRate> getExchangeRates() {
		String uuid = UUID.randomUUID().toString();

		return Flux.<ExchangeRate>create(sink -> subscribers.put(uuid, sink))
				.doOnTerminate(() -> subscribers.remove(uuid));
	}

	private Message<ExchangeRate> mapExchangeRate(ExchangeRate exchangeRate) {
		return MessageBuilder.withPayload(exchangeRate).setHeader(KafkaHeaders.MESSAGE_KEY,
				exchangeRate.getBaseCurrency() + "-" + exchangeRate.getQuoteCurrency()).build();
	}

	private ExchangeRate mapExchangeRate(OrderBookNotification orderBookNotification) {
		String[] currencies = orderBookNotification.getOrderBookEvent().getProductId().name().split("_");

		return new ExchangeRate(Currency.valueOf(currencies[0]), Currency.valueOf(currencies[1]),
				orderBookNotification.getOrderBook().getAsks().iterator().next().getPrice(),
				orderBookNotification.getOrderBook().getBids().iterator().next().getPrice(),
				orderBookNotification.getOrderBookEvent().getTime());
	}

	@Override
	public void destroy() throws Exception {
		if (!subscriber.isDisposed()) {
			subscriber.dispose();
		}
	}

}
