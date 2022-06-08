package ua.mike.micro.inventoryservice.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.mike.micro.inventoryservice.services.InventoryService;

/**
 * Created by mike on 02.06.2022 17:02
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {

    private static final long initDelaySeconds = 10;
    private static final long fixedRateSeconds = 30;

    private final InventoryService service;

    @Scheduled(initialDelay = initDelaySeconds * 1000, fixedRate = fixedRateSeconds * 1000)
    public void clearEmptyInventories() {
        service.removeEmptyInventories();
    }
}
