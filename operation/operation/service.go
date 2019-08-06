package operation

import (
	"encoding/json"
	"errors"
	"fmt"
	"gopkg.in/confluentinc/confluent-kafka-go.v1/kafka"
	"io.confluent/microservice-operation/kafkautil"
)

type Service struct {
	kafkaUtil           *kafkautil.KafkaUtil
	operationsByAccount map[string][]Operation
}

func NewService(kafkaUtil *kafkautil.KafkaUtil) *Service {
	return &Service{kafkaUtil: kafkaUtil, operationsByAccount: map[string][]Operation{}}
}

func (service *Service) GetOperations(account string, currency string) []Operation {
	accountOperations := service.operationsByAccount[account];

	if accountOperations == nil {
		return []Operation{}
	}

	var operations []Operation
	for i := 0; i < len(accountOperations); i++ {
		operation := accountOperations[i]
		if operation.DebitCurrency == currency || operation.CreditCurrency == currency {
			operations = append(operations, operation)
		}
	}

	return operations
}

func (service *Service) OnError(err error) {
	fmt.Printf(err.Error())
}

func (service *Service) OnNext(message *kafka.Message) error {
	var operation Operation
	if err := json.Unmarshal(message.Value, &operation); err != nil {
		return err
	}

	service.operationsByAccount[operation.Account] = append(service.operationsByAccount[operation.Account], operation)

	return nil
}

func (service *Service) SubmitOperation(operation *Operation) error {
	if operation.Type != "deposit" {
		if err := service.validateOperation(operation); err != nil {
			return err
		}
	}

	return service.kafkaUtil.Produce("operation", operation, kafka.PartitionAny)
}

func (service *Service) validateOperation(operation *Operation) error {
	previousOperations := service.operationsByAccount[operation.Account]

	// Assert there is enough funds for the currency.
	var totalFunds float64
	for i := 0; i < len(previousOperations); i++ {
		previousOperation := previousOperations[i]
		if previousOperation.DebitCurrency == operation.DebitCurrency {
			totalFunds -= previousOperation.DebitAmount
		}
		if previousOperation.CreditCurrency == operation.DebitCurrency {
			totalFunds += previousOperation.CreditAmount
		}
	}

	if totalFunds < operation.DebitAmount {
		return errors.New("not enough funds")
	}

	return nil
}
