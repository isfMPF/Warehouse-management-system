package com.project.wms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;  //код товара
    private String name;
    private String volume; //объём
    private String unit; //л, кг
    private int quantity; //кол-во в упаковке
    private Double price;
    private int amount; //кол-во на складе

}
