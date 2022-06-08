package ua.mike.micro.inventoryservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PositionDto {

    private UUID id;
    private UUID beerId;
    private Integer orderedQty;
    private Integer allocatedQty;
}
