package ua.mike.micro.inventoryservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.mike.micro.inventoryservice.models.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepo extends JpaRepository<Inventory, UUID> {

    List<Inventory> findInventoriesByBeerId(UUID beerID);
}
