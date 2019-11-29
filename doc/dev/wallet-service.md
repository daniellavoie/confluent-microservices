# Wallet Service development instructions

The Wallet Service is a Kafka Stream application leveraging Spring Boot. Such application results in a runnable JAR file after being built. Configuration can be applied through environment variables and command line arguments.

## Build

### Prerequisites

* Java 11 (see specific instruments for Java 8)

The Wallet Service is built with maven. A maven wrapper is embedded in the project so it can be built just by launching the following command:

```
$ ./wallet/mvnw clean package -f wallet/pom.xml
```

### Building with Java 8

If for some reason you are unable to access Java 11. You can update the `java.version` property from `wallet/pom.xml` to `1.8` and run the commands instructed previously.

## Run Integration tests

Running integration tests requires a kafka cluster as well as a running instance of the Wallet Service. The following instructions will get you covered to setup the cluster and run the application localy.

### Prerequisites

* Docker
* docker-compose
* Java 11
* curl

### Starting Kafka locally

```
$ docker-compose -f docker-compose/dev/docker-compose.yml
```

### Starting the Wallet Service

After having built the Wallet Service, you can run the application with the following java command:

```
$ java -jar wallet-it/target/wallet.jar
```

### Run the integration tests

Integration tests runs within the testing phase of a dedicated maven project. These tests runs as a standalone application that asserts the Kafka outputs produces by the Rate Service. Default configuration assumes your kafka cluster and your Wallet Service are running locally.

```
$ ./integration-tests/wallet-it/mvnw test -f integration-tests/wallet-it/pom.xml
```
### Running integration tests with Java 8

If for some reason you are unable to access Java 11. You can update the `java.version` property from `integration-tests/wallet-it/pom.xml` to `1.8` and run the commands instructed previously.

### Pointing the integration tests to a deployed environment

You may certainly be hoping to test your service against an environment that is not on your machine but rather deployed. To do so, you may configure the targeted kafka cluster with environment variables. Here is an example to run the tests against a custom kafka environment.

```
$ export SPRING_CLOUD_STREAM_KAFKA_BINDER_BROKERS=my-kafka-server-hostname:9021
$ export SPRING_CLOUD_STREAM_KAFKA_BINDER_CONSUMERPROPERTIES_SCHEMA_REGISTRY_URL=my-schema-registry-url

$ ./integration-tests/wallet-it/mvnw test -f integration-tests/wallet-it/pom.xml
```

The CI / CD pipeline showcased with this project leverages these environments variables to test an environment after being deployed.

## Update a new schema

See the dedicated section [Schema Upgrades](doc/dev/schema-upgrades.md).