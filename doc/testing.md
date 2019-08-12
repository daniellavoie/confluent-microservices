# Testing

* [Unit Testing](#unit-testing)
* Integration Testing
* Acceptance Testing

## Unit Testing

These test aims to validate the service atomically behaves as expected.

Unit test follows the following caracteristics:

* Should not require any external dependencies (no database, no other micro service)
* Runs quickly (seconds or few minutes)
* Runs during the build process

### Example tests

The Currency Spotter service includes unit tests. They can be exectuted by following the [service development instructions](../currency-spotter/README.md#development).

## Integration Testing

Integration is an end to end state of a service with its coupled dependencies. In the context of an Event Driven Microservice architecture decoupled, this stops at Kafka. As such, integration tests only needs to validate the messages sent my the targeted service. 

More specifically, the following characteristics applies to the tests:

* Publishes message to Kafka with Schema Registry validation
* Requires a predictive data set.
* Tests needs to be idempotent.

## Acceptance Testing