package ua.mike.micro.inventoryservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mike.micro.inventoryservice.services.InventoryService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory/beer")
public class InventoryController {

    private final InventoryService service;

    @GetMapping("/{beerId}")
    public Integer getQtyOnHand(@PathVariable UUID beerId) {
        return service.getQtyOnHand(beerId);
    }
}
