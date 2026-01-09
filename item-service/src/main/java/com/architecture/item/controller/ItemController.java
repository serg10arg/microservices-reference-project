package com.architecture.item.controller;

import com.architecture.item.model.Item;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    public List<Item> itemList = new ArrayList<>();

    public ItemController() {
        itemList.add(new Item(1L, "Laptop Gamer"));
        itemList.add(new Item(2L, "Mouse Optico"));
    }

    @GetMapping
    public List<Item> getAllItems() {
        return itemList;
    }

    @GetMapping("/{id}")
    public Optional<Item>getItemById(@PathVariable Long id) {

        return itemList.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    @PostMapping
    public Item createItem(@RequestBody Item item) {
        item.setId((long) (itemList.size() +1 ));
        itemList.add(item);
        return item;
    }
}
