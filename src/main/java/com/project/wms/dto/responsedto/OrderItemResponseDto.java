package com.project.wms.dto.responsedto;

import com.project.wms.entity.OrderEntity;
import com.project.wms.entity.ProductEntity;
import lombok.Data;

@Data
public class OrderItemResponseDto {

    private Long id;
    private Long order; // Связанный заказ
    private Long product; // Связанный товар
    private int amount; // Количество упаковок
    private Double price; // Цена за единицу товара
    private Double total; // Общая сумма для данного товара (amount * price)


}
