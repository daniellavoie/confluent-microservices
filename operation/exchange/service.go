package exchange

import (
	"encoding/json"
	"errors"
	"fmt"
	"gopkg.in/confluentinc/confluent-kafka-go.v1/kafka"
	"io.confluent/microservice-operation/operation"
	"io.confluent/microservice-operation/rate"
	"log"
)

type Service struct {
	operationService *operation.Service
	rates            map[string]*rate.Rate
}

func buildRateIdentifier(baseCurrency string, quoteCurrency string) string {
	return baseCurrency + "-" + quoteCurrency
}

func NewService(operationService *operation.Service) *Service {
	return &Service{operationService: operationService, rates: map[string]*rate.Rate{}}
}

func (service *Service) getRate(baseCurrency string, quoteCurrency string) (*rate.Rate, error) {
	rate := service.rates[buildRateIdentifier(baseCurrency, quoteCurrency)]

	if rate == nil {
		rate = service.rates[buildRateIdentifier(quoteCurrency, baseCurrency)]
	}

	if rate == nil {
		return nil, errors.New(fmt.Sprintf("rate %s/%s could not be found", baseCurrency, quoteCurrency))
	}

	return rate, nil
}

func (service *Service) OnError(err error) {
	fmt.Printf(err.Error())
}

func (service *Service) OnNext(message *kafka.Message) error {
	var rate rate.Rate
	if err := json.Unmarshal(message.Value, &rate); err != nil {
		log.Printf("Failed to unmarshal rate. Error : %s", err.Error())

		return err
	}

	service.rates[buildRateIdentifier(rate.BaseCurrency, rate.QuoteCurrency)] = &rate

	return nil
}

func (service *Service) ProcessRequest(request *Request) error {
	rate, err := service.getRate(request.BaseCurrency, request.ExchangeCurrency)
	if err != nil {
		return err
	}

	var amount float64
	if rate.BaseCurrency == request.BaseCurrency {
		amount = request.Amount * rate.Ask
	} else {
		amount = request.Amount / rate.Bid
	}

	exchangeOperation := operation.NewExchangeOperation(request.Account, amount, request.ExchangeCurrency, request.Amount, request.BaseCurrency)

	return service.operationService.SubmitOperation(exchangeOperation)
}
