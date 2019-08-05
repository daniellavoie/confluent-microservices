package funds

import (
	"io.confluent/microservice-operation/operation"
)

type Service struct {
	operationService *operation.Service
}

func NewService(operationService *operation.Service) *Service {
	return &Service{operationService: operationService}
}

func (service *Service) ProcessRequest(request *Request) error {
	var fundOperation *operation.Operation
	if request.Action == "withdraw" {
		fundOperation = operation.NewWithdrawOperation(request.Account, request.Amount, request.Currency)
	} else {
		fundOperation = operation.NewDepositOperation(request.Account, request.Amount, request.Currency)
	}

	return service.operationService.SubmitOperation(fundOperation)
}
