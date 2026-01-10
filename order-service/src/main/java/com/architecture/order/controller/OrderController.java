package com.architecture.order.controller;

import com.architecture.order.model.Order;
import com.architecture.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order placeOrder(@RequestBody Order order) {
        // Delegamos al servicio que maneja la lógica y el evento
        return orderService.createOrder(order);
    }
}
