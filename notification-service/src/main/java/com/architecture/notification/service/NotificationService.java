package com.architecture.notification.service;

import com.architecture.notification.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationService {

    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Escucha el tópico 'orders'.
     * El groupId asegura que si escalamos este servicio, los mensajes se repartan
     * entre las instancias y no se dupliquen.
     *
     */
    @KafkaListener(topics = "orders", groupId = "notification-group")
    public void consume(OrderEvent event) {
        // Simulación de lógica de negocio (ej. enviar email real)
        logger.info("Notificación recibida para el Pedido ID: {} con estatus: {}",
                event.getOrderId(),
                event.getOrderStatus());

        // Aquí podrías agregar lógica para enviar un email, SMS, o push notification
    }
}
