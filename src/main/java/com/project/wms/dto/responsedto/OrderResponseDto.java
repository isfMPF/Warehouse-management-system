package com.project.wms.dto.responsedto;

import com.project.wms.entity.ClientEntity;
import com.project.wms.entity.OrderItemEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderResponseDto {

    private int id;
    private LocalDate date;
    private String clientName;
    private Long codeClient;
    private Double total; //сумма заказа
    private List<OrderItemResponseDto> items; // Список товаров для этого заказа
}
