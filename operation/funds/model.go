package funds

type Request struct {
	Account          string  `json:"account"`
	Action           string  `json:"action"`
	Currency         string  `json:"currency"`
	Amount           float64 `json:"amount"`
	BankAccount      string  `json:"bankAccount"`
	CreditCardNumber string  `json:"creditCardNumber"`
}
