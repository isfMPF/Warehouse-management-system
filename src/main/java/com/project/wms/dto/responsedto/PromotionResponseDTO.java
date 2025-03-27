package com.project.wms.dto.responsedto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class PromotionResponseDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer requiredQuantity;
    private Integer freeQuantity;

    private String freeProductCode;
    private String freeProductVolume;
    private String freeProductName;

    private String requiredProductCode;
    private String requiredProductVolume;
    private String requiredProductName;

    private Set<String> includedProductCodes;
    private Set<String> includedProductNames;
    private Set<String> includedProductVolumes;

    private Boolean isActive;
}
