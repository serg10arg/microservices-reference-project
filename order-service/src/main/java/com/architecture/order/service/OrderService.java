package com.architecture.order.service;

import com.architecture.order.event.OrderEvent;
import com.architecture.order.model.Order; // Asumiremos un modelo simple similar a Item
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public Order createOrder(Order order) {
        // 1. Simular lógica de negocio (asignar ID, guardar en DB, etc.)
        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus("CREATED");

        // 2. Crear el evento
        OrderEvent event = new OrderEvent(order.getOrderId(), order.getStatus());

        // 3. Publicar el evento en el tópico "orders" de manera asíncrona
        // Fuente: "Propagate this event to a Kafka topic named 'orders'"
        kafkaTemplate.send("orders", event);

        System.out.println("Evento enviado a Kafka para OrderId: " + order.getOrderId());

        return order;
    }
}
