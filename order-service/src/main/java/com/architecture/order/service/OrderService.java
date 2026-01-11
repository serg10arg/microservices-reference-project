package com.architecture.order.service;

import com.architecture.order.event.OrderEvent;
import com.architecture.order.model.Order;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // Importación clave para comunicación HTTP

import java.util.UUID;

@Service
public class OrderService {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final RestTemplate restTemplate; // Cliente HTTP con balanceo de carga

    // Inyección de dependencias por constructor
    public OrderService(KafkaTemplate<String, OrderEvent> kafkaTemplate, RestTemplate restTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }

    /**
     * Crea un pedido solo si hay stock disponible.
     * Protegido por el patrón Circuit Breaker.
     *
     * @param order Datos del pedido entrante
     * @return El pedido procesado o una respuesta de fallback
     */
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackCreateOrder")
    public Order createOrder(Order order) {

        // 1. Definir la URL usando el Nombre del Servicio (NO localhost)
        // Gracias a @LoadBalanced y Eureka, "inventory-service" se resuelve dinámicamente a la IP real.
        // Asumimos que el nombre del producto es el SKU para este ejemplo.
        String skuCode = order.getProductName();
        String inventoryUrl = "http://inventory-service/api/inventory/" + skuCode;

        // 2. Llamada Síncrona al Microservicio de Inventario
        // Uso de RestTemplate para comunicación entre servicios vía Eureka
        Boolean inStock = restTemplate.getForObject(inventoryUrl, Boolean.class);

        // 3. Validación de Negocio
        if (Boolean.FALSE.equals(inStock)) {
            throw new RuntimeException("El producto " + skuCode + " no tiene stock disponible.");
        }

        // 4. Si hay stock, procedemos con la creación del pedido
        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus("CREATED");

        // 5. Comunicación Asíncrona: Notificar al resto del sistema vía Kafka
        // Envío de evento al tópico "orders"
        OrderEvent event = new OrderEvent(order.getOrderId(), order.getStatus());
        kafkaTemplate.send("orders", event);

        System.out.println("Pedido creado exitosamente: " + order.getOrderId());
        return order;
    }

    // --- Método Fallback ---
    // Se ejecuta si:
    // a) El inventory-service está caído (Connection refused).
    // b) El inventory-service tarda mucho (Timeout).
    // c) El circuito está ABIERTO por fallos previos.
    // Implementación de fallback para degradación de servicio.
    public Order fallbackCreateOrder(Order order, Throwable t) {
        System.err.println("FALLBACK EJECUTADO. Razón: " + t.getMessage());

        order.setOrderId("00000");
        // Indicamos que falló no por lógica de negocio, sino por indisponibilidad o error técnico
        order.setStatus("FAILED_INVENTORY_UNAVAILABLE");

        return order;
    }
}
