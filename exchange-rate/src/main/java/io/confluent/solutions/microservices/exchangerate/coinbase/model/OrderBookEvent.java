package io.confluent.solutions.microservices.exchangerate.coinbase.model;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderBookEvent {
	private final OrderBookEventType type;
	private final ProductId productId;
	private final LocalDateTime time;
	private final String[][] asks;
	private final String bestAsk;
	private final String bestBid;
	private final String[][] bids;
	private final String[][] changes;
	private final CoinbaseChannel[] channels;
	private final String high24h;
	private final String lastSize;
	private final String low24h;
	private final String open24h;
	private final String price;
	private final long sequence;
	private final Side side;
	private final long tradeId;
	private final String volume24h;
	private final String volume30d;

	@JsonCreator
	public OrderBookEvent(@JsonProperty("type") OrderBookEventType type,
			@JsonProperty("product_id") ProductId productId, @JsonProperty("time") LocalDateTime time,
			@JsonProperty("asks") String[][] asks, @JsonProperty("best_ask") String bestAsk,
			@JsonProperty("best_bid") String bestBid, @JsonProperty("bids") String[][] bids,
			@JsonProperty("changes") String[][] changes, @JsonProperty("channels") CoinbaseChannel[] channels,
			@JsonProperty("high_24h") String high24h, @JsonProperty("last_size") String lastSize,
			@JsonProperty("low_24h") String low24h, @JsonProperty("open_24h") String open24h,
			@JsonProperty("price") String price, @JsonProperty("sequence") long sequence,
			@JsonProperty("side") Side side, @JsonProperty("trade_id") long tradeId,
			@JsonProperty("volume_24h") String volume24h, @JsonProperty("volume_30d") String volume30d) {
		this.type = type;
		this.productId = productId;
		this.time = time;
		this.asks = asks;
		this.bestAsk = bestAsk;
		this.bestBid = bestBid;
		this.bids = bids;
		this.changes = changes;
		this.channels = channels;
		this.high24h = high24h;
		this.lastSize = lastSize;
		this.low24h = low24h;
		this.open24h = open24h;
		this.price = price;
		this.sequence = sequence;
		this.side = side;
		this.tradeId = tradeId;
		this.volume24h = volume24h;
		this.volume30d = volume30d;
	}

	public OrderBookEventType getType() {
		return type;
	}

	public ProductId getProductId() {
		return productId;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public String[][] getAsks() {
		return asks;
	}

	public String getBestAsk() {
		return bestAsk;
	}

	public String getBestBid() {
		return bestBid;
	}

	public String[][] getBids() {
		return bids;
	}

	public String[][] getChanges() {
		return changes;
	}

	public CoinbaseChannel[] getChannels() {
		return channels;
	}

	public String getHigh24h() {
		return high24h;
	}

	public String getLastSize() {
		return lastSize;
	}

	public String getLow24h() {
		return low24h;
	}

	public String getOpen24h() {
		return open24h;
	}

	public String getPrice() {
		return price;
	}

	public long getSequence() {
		return sequence;
	}

	public Side getSide() {
		return side;
	}

	public long getTradeId() {
		return tradeId;
	}

	public String getVolume24h() {
		return volume24h;
	}

	public String getVolume30d() {
		return volume30d;
	}

	@Override
	public String toString() {
		return "OrderBookEvent [type=" + type + ", productId=" + productId + ", time=" + time + ", asks="
				+ Arrays.toString(asks) + ", bestAsk=" + bestAsk + ", bestBid=" + bestBid + ", bids="
				+ Arrays.toString(bids) + ", changes=" + Arrays.toString(changes) + ", channels="
				+ Arrays.toString(channels) + ", high24h=" + high24h + ", lastSize=" + lastSize + ", low24h=" + low24h
				+ ", open24h=" + open24h + ", price=" + price + ", sequence=" + sequence + ", side=" + side
				+ ", tradeId=" + tradeId + ", volume24h=" + volume24h + ", volume30d=" + volume30d + "]";
	}

}
