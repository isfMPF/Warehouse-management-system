package com.project.wms.dto.requestdto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ClientRequestDto {
    private Long id;
    @NotEmpty(message = "Вводите код куллера")
    @Pattern(regexp = "\\d+", message = "Только цифры")
    private String codeClient; // код холодильника
    @NotEmpty(message = "Вводите название трговой точки")
    private String name; //название торговой точки
    @NotEmpty(message = "Вводите адрес")
    private String address;
    @NotEmpty(message = "Вводите ФИО")
    private String fio;
    @NotEmpty(message = "Вводите номер телефона")
    @Pattern(regexp = "\\d+", message = "Только цифры")
    private String phone;
}
