package com.project.wms.dto.requestdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductArrivalDto {
    @NotEmpty(message = "Выберите товар")
    private String code;

    @NotNull(message = "Введите количество")
    @Min(value = 1, message = "Количество должно быть больше 0")
    private Integer amountToAdd;
}