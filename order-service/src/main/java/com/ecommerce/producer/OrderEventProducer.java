package com.ecommerce.producer;

import com.ecommerce.event.OrderPlacedEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventProducer {

    private static final String TOPIC = "order-placed";

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderPlaced(OrderPlacedEvent event) {

        // kafkaTemplate.send(topic, key, value);
        kafkaTemplate.send(
                TOPIC,
                event.orderNumber(),
                event
        ).whenComplete((result, ex) -> {

            if (ex != null) {
                log.error(
                        "Failed to publish order event: {}",
                        event.orderNumber(), ex);
            } else {
                log.info(
                        "Order event published: {}, partition: {}, offset: {}",
                        event.orderNumber(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}

/*
    The send() method returns a CompletableFuture.
    The whenComplete() callback allows us to determine whether Kafka acknowledged the message or publishing failed.
 */

/*
    ***Why use orderNumber as the message key?
    --------------------------------------------------------------------------------
    Events belonging to the same order will normally be routed to the same partition,
    assuming the partition configuration remains unchanged.

    For example:
    Illustrative partition assignment

    Partition 0 ---  Order A: Placed   Order A: Confirmed
    Partition 1 ---  Order B: Placed   Order B: Cancelled
    Partition 2 ---  Order C: Placed   Order C: Confirmed

    Kafka guarantees ordering within a partition, not across the entire topic.
 */