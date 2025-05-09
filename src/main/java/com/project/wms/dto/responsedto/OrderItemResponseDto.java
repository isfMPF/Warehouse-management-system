package com.project.wms.dto.responsedto;

import lombok.Data;

@Data
public class OrderItemResponseDto {

    private Long id;
    private Long order; // Связанный заказ
    private Long code; // Связанный товар
    private String name;
    private String volume; //объём
    private int quantity;
    private int amount; // Количество упаковок
    private Double price; // Цена за единицу товара
    private Double total; // Общая сумма для данного товара (amount * price)
    private Double weight;

}
