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



Integration testing represents the 

* Publishes message to Kafka with Schema Registry validation
* Requires a predictive data set.
* Tests needs to be idempotent.

## Acceptance Testing