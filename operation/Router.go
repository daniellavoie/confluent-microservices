package main

import (
	"errors"
	"github.com/gin-gonic/gin"
	"io.confluent/microservice-operation/exchange"
	"io.confluent/microservice-operation/funds"
	"io.confluent/microservice-operation/operation"
	"net/http"
)

type Router struct {
	fundsService     *funds.Service
	exchangeService  *exchange.Service
	operationService *operation.Service
}

func NewRouter(fundsService *funds.Service, exchangeService *exchange.Service, operationService *operation.Service) (*Router, error) {
	return &Router{fundsService: fundsService, exchangeService: exchangeService, operationService: operationService}, nil
}

func replyWithBadRequest(err error, c *gin.Context) {
	c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
}

func replyWithInternalError(err error, c *gin.Context) {
	c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
}

func (router *Router) GetOperationsRoute(c *gin.Context) {
	account := c.Param("account")
	if account == "" {
		replyWithBadRequest(errors.New("could not find account param"), c)
		return
	}

	currency := c.Param("currency")
	if currency == "" {
		replyWithBadRequest(errors.New("could not find currency param"), c)
		return
	}

	c.JSON(http.StatusOK, router.operationService.GetOperations(account, currency))
}

func (router *Router) ExchangeRoute(c *gin.Context) {
	var request *exchange.Request
	if err := c.ShouldBindJSON(&request); err != nil {
		replyWithInternalError(err, c)
		return
	}

	if err := router.exchangeService.ProcessRequest(request); err != nil {
		replyWithInternalError(err, c)
		return
	}
}

func (router *Router) FundsRoute(c *gin.Context) {
	var request *funds.Request
	if err := c.ShouldBindJSON(&request); err != nil {
		replyWithInternalError(err, c)
		return
	}

	if err := router.fundsService.ProcessRequest(request); err != nil {
		replyWithInternalError(err, c)
		return
	}
}
