package ua.mike.micro.inventoryservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ua.mike.micro.inventoryservice.dto.OrderDto;
import ua.mike.micro.inventoryservice.dto.events.AddedBeerNotification;
import ua.mike.micro.inventoryservice.models.Inventory;
import ua.mike.micro.inventoryservice.repo.InventoryRepo;
import ua.mike.micro.jms.Queue;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepo repo;
    private final JmsTemplate jms;

    @Override
    public Integer getQtyOnHand(UUID beerId) {
        return repo.findInventoriesByBeerId(beerId)
                .stream()
                .mapToInt(Inventory::getQuantityOnHand)
                .sum();
    }

    @Override
    public boolean allocate(OrderDto order) {
        log.debug("Allocating order {} ...", order.getId());
        final var totalOrdered = new AtomicInteger();
        final var totalAllocated = new AtomicInteger();
        order.getLines().stream().filter(line -> line.getOrderedQty() != null)
                .peek(line -> Optional.ofNullable(line.getAllocatedQty()).ifPresentOrElse(val -> {}, () -> line.setAllocatedQty(0)))
                .peek(line -> totalOrdered.addAndGet(line.getOrderedQty()))
                .forEach(line -> repo.findInventoriesByBeerId(line.getBeerId()).stream()
                        .filter(inventory -> inventory.getQuantityOnHand() != null)
                        .filter(inventory -> inventory.getQuantityOnHand() > 0)
                        .forEach(inventory -> {
                    //
                    final int qtyToAllocate = line.getOrderedQty() - line.getAllocatedQty();
                    if (inventory.getQuantityOnHand() >= qtyToAllocate) {
                        // full allocation
                        line.setAllocatedQty(line.getOrderedQty());
                        inventory.setQuantityOnHand(inventory.getQuantityOnHand() - qtyToAllocate);
                    } else {
                        // partial allocation
                        line.setAllocatedQty(line.getAllocatedQty() + inventory.getQuantityOnHand());
                        inventory.setQuantityOnHand(0);
                    }
                    totalAllocated.addAndGet(line.getAllocatedQty());
                    repo.save(inventory);
                }));
        log.debug("Total ordered: {} Total allocated: {}", totalOrdered, totalAllocated);
        return totalOrdered.get() == totalAllocated.get();
    }

    @Override
    public void deAllocate(OrderDto order) {
        log.debug("Deallocating order {} ...", order.getId());
        order.getLines().forEach(line -> {
            final var inventory = Inventory.builder()
                    .beerId(line.getBeerId())
                    .quantityOnHand(line.getAllocatedQty())
                    .build();
            final var saved = repo.save(inventory);
            log.debug("Saved inventory [{}] for beer {} , qty: {}", saved.getId(), saved.getBeerId(), saved.getQuantityOnHand());
            jms.convertAndSend(Queue.ADDED_BEER_QUEUE, AddedBeerNotification.builder().beerId(saved.getBeerId()).build());
            log.debug("Sent added beer notification");
        });
    }

    @Override
    public void newInventory(UUID beerId, Integer qty) {
        log.debug("Creating new inventory ...");
        final var inventory = Inventory.builder()
                .beerId(beerId)
                .quantityOnHand(qty)
                .build();
        final var saved = repo.save(inventory);
        log.debug("Saved inventory [{}] for beer {} , qty: {}", saved.getId(), saved.getBeerId(), saved.getQuantityOnHand());
        jms.convertAndSend(Queue.ADDED_BEER_QUEUE, AddedBeerNotification.builder().beerId(saved.getBeerId()).build());
        log.debug("Sent added beer notification");
    }

    @Override
    public void removeEmptyInventories() {
        log.debug("Searching empty inventories ...");
        repo.findAll().stream()
                .filter(inventory -> inventory.getQuantityOnHand() != null)
                .filter(inventory -> inventory.getQuantityOnHand() == 0)
                .forEach(inventory -> {
                    repo.delete(inventory);
                    log.debug("Removed empty inventory {} for beer {}", inventory.getId(), inventory.getBeerId());
                });
    }
}
