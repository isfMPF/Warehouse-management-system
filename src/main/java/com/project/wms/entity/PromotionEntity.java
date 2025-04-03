package com.project.wms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "promotions")
public class PromotionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "required_quantity", nullable = false)
    private Integer requiredQuantity;

    @Column(name = "free_quantity", nullable = false)
    private Integer freeQuantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "free_product_code", referencedColumnName = "code")
    private ProductEntity freeProduct;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "required_product_code", referencedColumnName = "code")
    private ProductEntity requiredProduct;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.EAGER)
    private Set<PromotionProduct> includedProducts = new HashSet<>();

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public void addIncludedProduct(ProductEntity product) {
        if (product == null) {
            throw new IllegalArgumentException("Продукт не может быть null");
        }

        // Проверяем, есть ли уже этот продукт в списке
        boolean exists = includedProducts.stream()
                .anyMatch(promotionProduct -> promotionProduct.getProduct().getCode().equals(product.getCode()));

        if (!exists) {
            PromotionProduct promotionProduct = new PromotionProduct();
            promotionProduct.setPromotion(this);
            promotionProduct.setProduct(product);
            includedProducts.add(promotionProduct);
        }
    }
}

