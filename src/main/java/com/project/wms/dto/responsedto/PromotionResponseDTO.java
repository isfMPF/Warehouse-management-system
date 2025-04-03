package com.project.wms.dto.responsedto;

import com.project.wms.entity.PromotionProduct;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class PromotionResponseDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer requiredQuantity;
    private Integer freeQuantity;
    private ProductResponseDto freeProduct;
    private ProductResponseDto requiredProduct;
    private Set<PromotionProductResponseDto> includedProducts;  // Объединенная информация о товарах

    private Integer displayRequiredQuantity;
    private Boolean isActive;

}