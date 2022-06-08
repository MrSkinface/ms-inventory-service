package ua.mike.micro.inventoryservice.jms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ua.mike.micro.inventoryservice.dto.events.AllocateOrderRequest;
import ua.mike.micro.inventoryservice.dto.events.AllocateOrderResult;
import ua.mike.micro.inventoryservice.dto.events.DeAllocateOrderRequest;
import ua.mike.micro.inventoryservice.dto.events.NewInventoryRequest;
import ua.mike.micro.inventoryservice.services.InventoryService;
import ua.mike.micro.jms.JmsConsumerActions;
import ua.mike.micro.jms.Queue;

/**
 * Created by mike on 01.06.2022 21:52
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JmsListeners {

    private final InventoryService service;
    private final JmsTemplate jms;
    private final JmsConsumerActions actions;

    @JmsListener(destination = Queue.ALLOCATE_ORDER_QUEUE)
    public void listenToAllocation(String data) {
        actions.consume(data, AllocateOrderRequest.class, request -> {
            boolean allocationError = false;
            boolean pendingInventory = false;
            try {
                final boolean fullAllocation = service.allocate(request.getOrder());
                if (!fullAllocation) pendingInventory = true;
            } catch (Exception e) {
                log.error("Allocation failed for order {}: {}", request.getOrder().getId(), e);
                allocationError = true;
            }
            jms.convertAndSend(Queue.ALLOCATE_ORDER_RESPONSE_QUEUE, AllocateOrderResult.builder()
                    .order(request.getOrder()).allocationError(allocationError).pendingInventory(pendingInventory).build());
        });
    }

    @JmsListener(destination = Queue.DEALLOCATE_ORDER_QUEUE)
    public void listenToDeAllocation(String data) {
        actions.consume(data, DeAllocateOrderRequest.class, request -> {
            service.deAllocate(request.getOrder());
        });
    }

    @JmsListener(destination = Queue.NEW_INVENTORY_QUEUE)
    public void newInventory(String data) {
        actions.consume(data, NewInventoryRequest.class, request -> {
            service.newInventory(request.getBeerId(), request.getBrewed());
        });
    }
}
