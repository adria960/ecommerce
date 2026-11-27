package com.ecommerce.service;

import com.ecommerce.event.OrderPlacedEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void sendOrderNotification(OrderPlacedEvent event) {

        // For simplicity, we simulate email delivery using logging.
        log.info(
                "Sending notification to customer {} for order {}. Total: {}",
                event.customerId(),
                event.orderNumber(),
                event.totalAmount()
        );
    }
}