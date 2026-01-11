package com.architecture.order.service;

import com.architecture.order.event.OrderEvent;
import com.architecture.order.model.Order;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderService(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Aplicamos el patrón Circuit Breaker.
     * Nombre: "inventory" (coincide con application.properties)
     * Fallback: "fallbackCreateOrder" (método que se ejecuta si hay fallo)
     * Fuente: Uso de anotación @CircuitBreaker y fallbackMethod
     */
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackCreateOrder")
    public Order createOrder(Order order) {
        // 1. Simulación de llamada externa a Inventario (Propenso a fallos)
        // En un caso real, aquí usarías RestTemplate o WebClient
        simularVerificacionInventario();

        // 2. Lógica normal
        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus("CREATED");

        OrderEvent event = new OrderEvent(order.getOrderId(), order.getStatus());
        kafkaTemplate.send("orders", event);

        System.out.println("Pedido creado exitosamente: " + order.getOrderId());
        return order;
    }

    // --- Método Fallback ---
    // IMPORTANTE: Debe tener la misma firma que el original + un parámetro Throwable
    public Order fallbackCreateOrder(Order order, Throwable t) {
        System.out.println("¡CIRCUITO ABIERTO O ERROR! Ejecutando Fallback. Razón: " + t.getMessage());

        // Devolvemos una respuesta "degradada" pero válida, o un aviso de error controlado
        order.setOrderId("00000");
        order.setStatus("FAILED_INVENTORY_DOWN");
        // No enviamos evento a Kafka para no ensuciar el sistema

        return order;
    }

    // Método auxiliar para simular fallos (Chaos Engineering casero)
    private void simularVerificacionInventario() {
        // Simulamos que el sistema falla el 50% de las veces aleatoriamente
        if (Math.random() > 0.5) {
            throw new RuntimeException("El servicio de inventario no responde (Simulado)");
        }
        System.out.println("Verificación de inventario exitosa.");
    }
}
