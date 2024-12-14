package com.project.wms.dto.requestdto;

import lombok.Data;

@Data
public class OrderItemRequestDto {

    private Long productId;
    private int amount; // Количество упаковок


}
