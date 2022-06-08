package ua.mike.micro.inventoryservice.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.mike.micro.inventoryservice.dto.OrderDto;

/**
 * Created by mike on 02.06.2022 13:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeAllocateOrderRequest {

    private OrderDto order;
}
