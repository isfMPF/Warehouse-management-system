package com.project.wms.dto.responsedto;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long id;
    private String code;  //код товара
    private String name;
    private String volume; //объём
    private String unit; //л, кг
    private int quantity; //кол-во в упаковке
    private Double price;
    private int amount; //кол-во на складе
    private Double weight;
}
