package ua.mike.micro.inventoryservice.services;

import ua.mike.micro.inventoryservice.dto.OrderDto;

import java.util.UUID;

public interface InventoryService {
    Integer getQtyOnHand(UUID beerId);

    boolean allocate(OrderDto order);
    void deAllocate(OrderDto order);

    void newInventory(UUID beerId, Integer qty);

    void removeEmptyInventories();
}
