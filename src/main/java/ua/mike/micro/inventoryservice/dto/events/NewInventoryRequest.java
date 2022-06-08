package ua.mike.micro.inventoryservice.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Created by mike on 08.06.2022 11:39
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewInventoryRequest {

    private UUID beerId;
    private Integer brewed;
}
