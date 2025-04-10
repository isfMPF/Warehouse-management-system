package com.project.wms.dto.requestdto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransportRequestDto {

    @NotEmpty(message = "Введите марку автомобиля")
    private String name;
    @NotEmpty(message = "Введите гос номер")
    private String number;
    @NotNull(message = "Введите грузоподъёмность")
    @DecimalMin(value = "0.0", message = "Значение должно быть больше или равно 0.0")
    private Double capacity;
}
