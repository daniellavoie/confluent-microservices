package main

import (
	"context"
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"gopkg.in/confluentinc/confluent-kafka-go.v1/kafka"
	"io.confluent/microservice-operation/exchange"
	"io.confluent/microservice-operation/funds"
	"io.confluent/microservice-operation/kafkautil"
	"io.confluent/microservice-operation/operation"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"
)

func main() {
	engine := gin.Default()

	kafkaUtil, err := kafkautil.NewKafkaUtil(loadKafkaConfig())

	if err != nil {
		panic(err)
	}

	operationService := operation.NewService(kafkaUtil)
	fundsService := funds.NewService(operationService)
	exchangeService := exchange.NewService(operationService)

	var listeners = map[string]kafkautil.Listener{}
	listeners["exchange-rate"] = exchangeService
	listeners["operation"] = operationService

	if err := kafkaUtil.Subscribe([]string{"exchange-rate", "operation"}, listeners); err != nil {
		shutdownService(kafkaUtil, nil)

		panic(err)
	}

	router, err := NewRouter(fundsService, exchangeService, operationService)

	if err != nil {
		shutdownService(kafkaUtil, nil)

		panic(err)
	}

	engine.Use(cors.Default())
	engine.POST("/funds", router.FundsRoute)
	engine.POST("/exchange", router.ExchangeRoute)
	engine.GET("/operation/:account/:currency", router.GetOperationsRoute)

	port := os.Getenv("SERVER_PORT")
	if port == "" {
		port = "8080"
	}

	srv := &http.Server{
		Addr:    ":" + port,
		Handler: engine,
	}

	go func() {
		log.Printf("Initializing web server.")

		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("listen: %s\n", err)
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	shutdownService(kafkaUtil, srv)
}

/*
func loadCorsConfig() cors.Config {
	allowOrigins := os.Getenv("OPERATION_CORS_ALLOW_ORIGINS");
	if allowOrigins == "" {
		allowOrigins = "http://localhost:4200"
	}

	allowOriginsArray := strings.Split(allowOrigins, ",")

	return cors.Config{
		AllowOrigins: allowOriginsArray,
		AllowMethods: []string{"POST", "GET", "OPTIONS", "DELETE", "PUT"},
		AllowHeaders: []string{"Origin"},
	}
}
	*/

func loadKafkaConfig() *kafka.ConfigMap {
	bootstrapServers := os.Getenv("OPERATION_KAFKA_BOOTSTRAP_SERVERS")
	groupId := os.Getenv("OPERATION_KAFKA_GROUP_ID")

	if bootstrapServers == "" {
		bootstrapServers = "localhost"
	}

	if groupId == "" {
		groupId = "operation"
	}

	return &kafka.ConfigMap{
		"bootstrap.servers": bootstrapServers,
		"group.id":          groupId,
		"auto.offset.reset": "earliest",
	}
}

func shutdownService(kafkaUtil *kafkautil.KafkaUtil, srv *http.Server) {

	if err := kafkaUtil.Close(); err != nil {
		log.Fatal("Failed to shutdown kafka resources: ", err)
	}

	if srv != nil {
		log.Println("Shutdown Server ...")

		ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()
		if err := srv.Shutdown(ctx); err != nil {
			log.Fatal("Server Shutdown: ", err)
		}

		log.Println("Server exiting")
	}
}
