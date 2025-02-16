package com.project.wms.dto.requestdto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequestDto {

    @NotNull(message = "Введите код товара")
    private String code;
    private int amount; // Количество упаковок
    private boolean selected;

}
