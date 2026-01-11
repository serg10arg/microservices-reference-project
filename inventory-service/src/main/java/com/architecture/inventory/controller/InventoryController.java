package com.architecture.inventory.controller;

import com.architecture.inventory.model.Inventory;
import com.architecture.inventory.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Endpoint consultado por Order Service.
     * Retorna true si hay stock, false si no.
     */
    @GetMapping("/{skuCode}")
    public boolean isInStock(@PathVariable String skuCode) {
        return inventoryRepository.findBySkuCode(skuCode)
                .map(inventory -> inventory.getQuantity() > 0)
                .orElse(false); // Si no existe el producto, retornamos false
    }

    // --- CARGA DE DATOS DE PRUEBA ---
    // Esto insertará datos automáticamente al iniciar la app
    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            inventoryRepository.save(new Inventory(null, "iphone_13", 100));
            inventoryRepository.save(new Inventory(null, "iphone_13_red", 0)); // Sin stock
            inventoryRepository.save(new Inventory(null, "laptop_gamer", 50));
        };
    }
}
