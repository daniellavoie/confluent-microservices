# Testing

* [Unit Testing](#unit-testing)
* [Integration Testing](#integration-testing)
* [Acceptance Testing](#acceptance-testing)

## Unit Testing

These test aims to validate the service atomically behaves as expected.

Unit test follows the following caracteristics:

* Should not require any external dependencies (no database, no other micro service)
* Runs quickly (seconds or few minutes)
* Runs during the build process

### Example tests

The Rate Service includes unit tests. They can be exectuted by following the [service development instructions](dev/rate-service.md#build).

## Integration Testing

Integration is an end to end state of a service with its coupled dependencies. In the context of an Event Driven Microservice architecture decoupled, this stops at Kafka. As such, integration tests only needs to validate the messages sent my the targeted service. 

More specifically, the following characteristics applies to the tests:

* Publishes message to Kafka with Schema Registry validation
* Requires a predictive data set.
* Tests needs to be idempotent.

## Acceptance Testing

Acceptance tests are based on jest and will run a headless chrome instance an crawl our UI Service to interract. They are intented to assert that a features are behaving as expected by business users. These could be used for gating before releasing to production. It's also possible to run these as smoke tests in production to assert if a service should be rollbacked or not.