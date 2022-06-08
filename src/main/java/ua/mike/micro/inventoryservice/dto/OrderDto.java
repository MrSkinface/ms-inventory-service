package ua.mike.micro.inventoryservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderDto {

    private UUID id;
    private List<PositionDto> lines;

}
