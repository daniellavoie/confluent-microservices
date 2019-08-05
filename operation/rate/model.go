package rate

const (
	BTC, USD, ETH, EUR = "BTC", "USD", "ETH", "EUR"
)

type Rate struct {
	BaseCurrency  string  `json:"baseCurrency"`
	QuoteCurrency string  `json:"quoteCurrency"`
	Ask           float64 `json:"ask"`
	Bid           float64 `json:"bid"`
	Timestamp     string  `json:"timestamp"`
}