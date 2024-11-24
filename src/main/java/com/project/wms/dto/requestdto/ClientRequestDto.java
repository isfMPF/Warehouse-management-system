package com.project.wms.dto.requestdto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ClientRequestDto {
    @NotEmpty(message = "Вводите код куллера")
    private String codeClient; // код холодильника
    @NotEmpty(message = "Вводите название трговой точки")
    private String name; //название торговой точки
    @NotEmpty(message = "Вводите адрес")
    private String address;
    @NotEmpty(message = "Вводите ФИО")
    private String fio;
    @NotEmpty(message = "Вводите номер телефона")
    private String phone;
}
