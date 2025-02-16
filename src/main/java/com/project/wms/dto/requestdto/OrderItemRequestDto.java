package com.project.wms.dto.requestdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequestDto {

    @NotNull(message = "Введите код товара")
    private String code;
    @NotNull(message = "Количество товара должно быть больше 0")
    @Min(value = 1)
    private int amount; // Количество упаковок
    private boolean selected;

}
