package com.ecommerce.consumer;

import com.ecommerce.event.OrderPlacedEvent;
import com.ecommerce.service.NotificationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Spring Kafka creates and manages a Kafka listener container behind the scenes.
 */

@Slf4j
@Component
public class OrderEventConsumer {

    private final NotificationService notificationService;

    public OrderEventConsumer(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "order-placed")
    public void consumeOrderPlaced(OrderPlacedEvent event) {

        log.info(
                "Received OrderPlacedEvent: {}",
                event.eventId());

        notificationService.sendOrderNotification(event);
    }
}

/*
    Stores OrderPlacedEvent

Kafka Consumer --->
Polls records from assigned partitions

    Deserializer --->
    JSON → OrderPlacedEvent

        @KafkaListener --->
        Invokes consumeOrderPlaced()

            NotificationService --->
            Processes the notification

                Offset commit --->
                Records successful consumer progress

    We do not need to implement the Kafka polling loop manually.
 */