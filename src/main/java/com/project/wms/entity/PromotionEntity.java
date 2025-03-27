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

    @ManyToOne
    @JoinColumn(name = "free_product_code", referencedColumnName = "code")
    private ProductEntity freeProduct;

    @ManyToOne
    @JoinColumn(name = "required_product_code", referencedColumnName = "code")
    private ProductEntity requiredProduct;

    @ManyToMany
    @JoinTable(
            name = "promotion_products",
            joinColumns = @JoinColumn(name = "promotion_id"),
            inverseJoinColumns = @JoinColumn(name = "product_code", referencedColumnName = "code")
    )
    private Set<ProductEntity> includedProducts = new HashSet<>();

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        LocalDate start = startDate;
        LocalDate end = endDate;

        return !today.isBefore(start) && !today.isAfter(end);
    }

}
