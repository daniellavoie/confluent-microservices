package kafkautil

import (
	"encoding/json"
	"errors"
	"fmt"
	"gopkg.in/confluentinc/confluent-kafka-go.v1/kafka"
	"log"
)

type Listener interface {
	OnError(err error)
	OnNext(message *kafka.Message) error
}

type KafkaUtil struct {
	producer *kafka.Producer
	consumer *kafka.Consumer
	run      bool
}

func NewKafkaUtil(configMap *kafka.ConfigMap) (*KafkaUtil, error) {
	producer, err := kafka.NewProducer(configMap)
	if err != nil {
		return nil, err
	}

	consumer, err := kafka.NewConsumer(configMap)
	if err != nil {
		return nil, err
	}

	return &KafkaUtil{producer: producer, consumer: consumer, run: true}, nil
}

func (kafkaUtil *KafkaUtil) Close() error {
	kafkaUtil.run = false

	kafkaUtil.producer.Close()

	return kafkaUtil.consumer.Close()
}

func (kafkaUtil *KafkaUtil) readKafkaMessages(listeners map[string]Listener, timeoutMs int) {
	var err error

	for kafkaUtil.run {
		if polledEvent := kafkaUtil.consumer.Poll(timeoutMs); polledEvent != nil {
			switch event := polledEvent.(type) {
			case *kafka.Message:
				listener := listeners[*event.TopicPartition.Topic]

				if listener == nil {
					err = errors.New(fmt.Sprintf("no listener found for topic %s", *event.TopicPartition.Topic))
					break
				}

				if err = listener.OnNext(event); err != nil {
					listener.OnError(err)
				}

				_, err = kafkaUtil.consumer.CommitMessage(event)
				if err != nil {
					listener.OnError(err)
				}
			case kafka.Error:
				log.Printf("Error while polling new events. Error code : %v. Message : %s", event.Code(), event.Error())
			default:
				fmt.Printf("Ignored %v\n", event)
			}
		}
	}
}

func (kafkaUtil *KafkaUtil) Produce(topic string, payload interface{}, partition int32) error {
	value, err := json.Marshal(payload)

	if err != nil {
		return err
	}

	err = kafkaUtil.producer.Produce(&kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &topic, Partition: kafka.PartitionAny},
		Value:          value,
	}, nil)

	return err
}

func (kafkaUtil *KafkaUtil) Subscribe(topics []string, listeners map[string]Listener) error {
	if err := kafkaUtil.consumer.SubscribeTopics(topics, nil); err != nil {
		return err
	}

	go kafkaUtil.readKafkaMessages(listeners, 1000)

	return nil
}
