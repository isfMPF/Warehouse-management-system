package com.project.wms.dto.responsedto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.SimpleTimeZone;

@Data
public class OrderResponseDtoToExport {

    private int id;
    private LocalDate date;
    private String clientName;
    private Long codeClient;
    private Double total; //сумма заказа
    private String forwarder;
    private String transport;
    private List<OrderItemResponseDto> item; // Список товаров для этого заказа
}
