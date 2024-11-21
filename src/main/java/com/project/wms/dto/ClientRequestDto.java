package com.project.wms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientRequestDto {
    @NotBlank(message = "Вводите код куллера")
    private int codeClient; // код холодильника
    @NotBlank(message = "Вводите название трговой точки")
    private String name; //название торговой точки
    @NotBlank(message = "Вводите адрес")
    private String address;
    @NotBlank(message = "Вводите ФИО")
    private String fio;
    @NotBlank(message = "Вводите номер телефона")
    private String phone;
}
