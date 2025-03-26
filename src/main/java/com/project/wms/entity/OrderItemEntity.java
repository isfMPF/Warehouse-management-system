package com.project.wms.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private OrderEntity order; // Связанный заказ

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_code", referencedColumnName = "code")
    private ProductEntity code; // Связанный товар

    private int amount; // Количество упаковок
    private Double price; // Цена за единицу товара
    private Double total; // Общая сумма для данного товара (amount * price)
    private Double weight;


}