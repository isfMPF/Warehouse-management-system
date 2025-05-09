package com.project.wms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product" , indexes = {
        @Index(name = "idx_product_code", columnList = "code") // Индекс на поле code
})

public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String code;  //код товара
    private String name;
    private String volume; //объём
    private String unit; //л, кг
    private int quantity; //кол-во в упаковке
    private Double price;
    private int amount; //кол-во на складе
    private Double weight;

    @Version  // Аннотация для оптимистичной блокировки!
    private Long version;

}
