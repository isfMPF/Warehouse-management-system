package com.project.wms.util;

import com.project.wms.dto.responsedto.ProductResponseDto;
import lombok.Data;

@Data
public class CatrItem {

    private ProductResponseDto product;
    private int amount;
}
