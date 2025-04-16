package com.project.wms.util.cart;

import com.project.wms.dto.responsedto.ProductResponseDto;
import lombok.Data;

@Data
public class CartItem {
    private ProductResponseDto product;
    private int amount;
}
