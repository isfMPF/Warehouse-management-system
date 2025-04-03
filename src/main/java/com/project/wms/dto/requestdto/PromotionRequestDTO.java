package com.project.wms.dto.requestdto;

import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.entity.ProductEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
public class PromotionRequestDTO {

    @NotBlank(message = "Название акции обязательно")
    @Size(max = 100, message = "Название акции не должно превышать 100 символов")
    private String name;

    @NotNull(message = "Дата начала обязательна")
    private LocalDate startDate;

    @NotNull(message = "Дата окончания обязательна")
    private LocalDate endDate;

    @NotNull(message = "Количество товаров для акции обязательно")
    @Min(value = 1, message = "Минимальное количество товаров для акции - 1")
    private Integer requiredQuantity;

    @NotNull(message = "Количество бесплатных товаров обязательно")
    @Min(value = 1, message = "Минимальное количество бесплатных товаров - 1")
    private Integer freeQuantity;

    @Valid
    @NotNull(message = "Информация о бесплатном товаре обязательна")
    private ProductEntity freeProduct;

    @Valid
    @NotNull(message = "Информация об обязательном товаре обязательна")
    private ProductEntity requiredProduct;

    @NotEmpty(message = "Список товаров для акции не может быть пустым")
    private Set<String> includedProducts;


}