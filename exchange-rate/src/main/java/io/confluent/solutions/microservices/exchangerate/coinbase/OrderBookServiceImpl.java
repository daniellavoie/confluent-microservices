package io.confluent.solutions.microservices.exchangerate.coinbase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.confluent.solutions.microservices.exchangerate.coinbase.converter.ChangeConverter;
import io.confluent.solutions.microservices.exchangerate.coinbase.converter.PriceConverter;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBook;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBookEntry;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBookEvent;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBookEventType;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.OrderBookNotification;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.ProductId;
import io.confluent.solutions.microservices.exchangerate.coinbase.model.Side;
import reactor.core.publisher.Flux;

@Service
public class OrderBookServiceImpl implements OrderBookService {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookServiceImpl.class);

	private final OrderBookEventService orderBookEventService;

	private final Map<ProductId, OrderBook> orderBooksByProductId = new HashMap<>();

	public OrderBookServiceImpl(OrderBookEventService orderBookEventService) {
		this.orderBookEventService = orderBookEventService;

		Arrays.stream(ProductId.values()).forEach(productId -> orderBooksByProductId.put(productId, new OrderBook()));
	}

	private OrderBookEntry mapAskToOrderBookEntry(String[] ask) {
		return PriceConverter.convertToOrderBookEntry(ask, Side.SELL);
	}

	private OrderBookEntry mapBidToOrderBookEntry(String[] bid) {
		return PriceConverter.convertToOrderBookEntry(bid, Side.BUY);
	}

	@Override
	public Flux<OrderBookNotification> orderBookStream(ProductId[] productIds) {
		// Step 1 - Subscribe to order book events
		return orderBookEventService.getEvents(productIds)

				// Step 2 - Update the actual state of the order book with the event
				.map(this::processOrderBookEvent)

				// Step 3 - Notify if the order book has changed.
				.filter(Optional::isPresent).map(Optional::get);
	}

	private void processL2Update(OrderBookEvent orderBookEvent, OrderBook orderBook) {

		List<OrderBookEntry> orderBookEntries = Arrays.stream(orderBookEvent.getChanges())
				.map(change -> ChangeConverter.convertToOrderBookEntry(change)).collect(Collectors.toList());

		orderBookEntries.forEach(orderBookEntry -> updateOrderBookForEntry(orderBook, orderBookEntry));
	}

	private Optional<OrderBookNotification> processOrderBookEvent(OrderBookEvent orderBookEvent) {
		try {
			if (!OrderBookEventType.L2_UPDATE.equals(orderBookEvent.getType())
					&& !orderBookEvent.getType().equals(OrderBookEventType.SNAPSHOT)) {
				LOGGER.debug("Ignoring event type {}.", orderBookEvent.getType());

				return Optional.empty();
			}

			OrderBook orderBook = orderBooksByProductId.get(orderBookEvent.getProductId());

			if (orderBook == null) {
				LOGGER.warn("Received an order book event for the unknown product {}.", orderBookEvent.getProductId());

				return Optional.empty();
			}

			return processOrderBookEvent(orderBookEvent, orderBook);

		} catch (Exception ex) {
			LOGGER.error("Failed to process " + orderBookEvent + ".", ex);

			return Optional.empty();
		}
	}

	private Optional<OrderBookNotification> processOrderBookEvent(OrderBookEvent orderBookEvent, OrderBook orderBook) {
		if (orderBookEvent.getType().equals(OrderBookEventType.L2_UPDATE)) {
			processL2Update(orderBookEvent, orderBook);

			return Optional.of(new OrderBookNotification(orderBook, orderBookEvent));
		} else if (orderBookEvent.getType().equals(OrderBookEventType.SNAPSHOT)) {
			processSnapshot(orderBookEvent, orderBook);

			return Optional.of(new OrderBookNotification(orderBook, orderBookEvent));
		} else {
			LOGGER.warn("Order book event type {} is not supported.", orderBookEvent.getType());

			return Optional.empty();
		}
	}

	private void processSnapshot(OrderBookEvent orderBookEvent, OrderBook orderBook) {
		Arrays.stream(orderBookEvent.getBids())

				.map(this::mapBidToOrderBookEntry)

				.forEach(orderBookEntry -> orderBook.getBids().add(orderBookEntry));

		Arrays.stream(orderBookEvent.getAsks())

				.map(this::mapAskToOrderBookEntry)

				.forEach(orderBookEntry -> orderBook.getAsks().add(orderBookEntry));
	}

	private void updateOrderBookForEntry(OrderBook orderBook, OrderBookEntry orderBookEntry) {
		Set<OrderBookEntry> entries = orderBookEntry.getSide().equals(Side.BUY) ? orderBook.getBids()
				: orderBook.getAsks();

		entries.stream().filter(existingEntry -> Double.compare(existingEntry.getPrice().doubleValue(),
				orderBookEntry.getPrice().doubleValue()) == 0).findFirst().ifPresent(entries::remove);

		if (orderBookEntry.getSize().doubleValue() > 0d) {
			entries.add(orderBookEntry);
		}
	}

}
