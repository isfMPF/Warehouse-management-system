package com.project.wms.dto.requestdto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductRequestDto {
    private Long id;
    @NotEmpty(message = "Введите код товара")
    private String code;  //код товара
    @NotEmpty(message = "Введите название товара")
    private String name;
    @NotEmpty(message = "Введите объём")
    private String volume; //объём
    @NotNull(message = "Выберите единицу измерения")
    private String unit; //л, кг
    @NotNull(message = "Введите количество в упаковке")
    @Min(value = 1, message = "Значение должно быть больше 0")
    private int quantity; //кол-во в упаковке
    @NotNull(message = "Введите цену товара")
    @DecimalMin(value = "0.0", message = "Значение должно быть больше или равно 0.0")
    private Double price;
    @NotNull(message = "Введите общую количество товара")
    @Min(value = 1, message = "Значение должно быть больше 0")
    private int amount; //кол-во на складе
}
