package com.project.wms.util;

import java.io.Serializable;
import java.util.Objects;

public class PromotionProductId implements Serializable {
    private Long promotion;  // Соответствует полю promotion в PromotionProduct
    private String product;  // Соответствует полю product (code) в PromotionProduct

    // Обязательный конструктор по умолчанию
    public PromotionProductId() {}

    // equals и hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PromotionProductId that = (PromotionProductId) o;
        return Objects.equals(promotion, that.promotion) &&
                Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(promotion, product);
    }
}