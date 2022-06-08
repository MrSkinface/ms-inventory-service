package ua.mike.micro.inventoryservice.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ua.mike.micro.inventoryservice.dto.BeerDto;
import ua.mike.micro.inventoryservice.models.Inventory;
import ua.mike.micro.inventoryservice.repo.InventoryRepo;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class Initializer implements CommandLineRunner {

    @Value("${ms.beer.service.host}")
    private String beerServiceUrl;
    private final InventoryRepo repo;
    private static final RestTemplate http = new RestTemplate();

    @Override
    public void run(String... args) throws Exception {
        if (repo.count() != 0) {
            log.warn("Repository is not empty, will not bootstrap anything");
            return;
        }
        final int[] someVal = {50};
        Optional.ofNullable(http.getForObject(beerServiceUrl + "/api/beer", BeerDto[].class)).ifPresent(array -> {
            Arrays.stream(array).forEach(beer -> {
                repo.save(Inventory.builder().beerId(beer.getId()).quantityOnHand(100 + someVal[0]).build());
                someVal[0] += 50;
            });
        });
        log.debug("Inventories loaded: {}", repo.count());
    }
}
