package com.project.wms.dto.requestdto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderRequestDto {

    @NotNull(message = "Клиент должен быть выбран")
    @Min(value = 1, message = "Идентификатор клиента должен быть положительным")
    private Long codeClient;
    @Valid
    List<OrderItemRequestDto> items = new ArrayList<>();

}
