# Currency Spotter

## Development

This project is built with maven.

### Pre requisites

This project leverages the Spring Boot framework and Spring Cloud Stream for Kafka integration.

No specific knowledge for it is required to build and run.

### Dependencies

* Java 11

### Running the application from an IDE

The application can be executed by running main method from the [CurrencySpotterApplication](src/main/java/io/confluent/solutions/microservices/currencyspotter/CurrencySpotterApplication.java) class.

### Run unit tests

```
$ ./mvnw clean test
```

### Package an executable binary

```
$ ./mvnw clean package
```

### Run the executable binary

```
$ java -jar target/currency-spotter.jar --spring.cloud.stream.kafka.binder.producerProperties.schema.registry.url=YOUR-SCHEMA-REGISTRY-URL
```

### Run Integration Tests

See the [Currency Spotter Integration Tests](../integration-tests/exchange-rate-it/README.md) on how to run integration tests.

