package exchange

type Request struct {
	Account          string  `json:"account"`
	BaseCurrency     string  `json:"baseCurrency"`
	ExchangeCurrency string  `json:"exchangeCurrency"`
	Amount           float64 `json:"amount"`
}


