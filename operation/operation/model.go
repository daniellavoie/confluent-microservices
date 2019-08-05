package operation

import "gopkg.in/confluentinc/confluent-kafka-go.v1/kafka"

type Operation struct {
	Account        string  `json:"account"`
	Type           string  `json:"type"`
	DebitAmount    float64 `json:"debitAmount"`
	DebitCurrency  string  `json:"debitCurrency"`
	CreditAmount   float64 `json:"creditAmount"`
	CreditCurrency string  `json:"creditCurrency"`
}

type Subscription struct {
	consumer *kafka.Consumer
	canceled bool
}

func NewDepositOperation(account string, amount float64, currency string) *Operation {
	return &Operation{
		Account:        account,
		Type:           "deposit",
		CreditAmount:   amount,
		CreditCurrency: currency,
	}
}

func NewExchangeOperation(account string, creditAmount float64, creditCurrency string, debitAmount float64, debitCurrency string) *Operation {
	return &Operation{
		Account:        account,
		Type:           "exchange",
		CreditAmount:   creditAmount,
		CreditCurrency: creditCurrency,
		DebitAmount:    debitAmount,
		DebitCurrency:  debitCurrency,
	}
}

func NewWithdrawOperation(account string, amount float64, currency string) *Operation {
	return &Operation{
		Account:       account,
		Type:          "withdraw",
		DebitAmount:   amount,
		DebitCurrency: currency,
	}
}
