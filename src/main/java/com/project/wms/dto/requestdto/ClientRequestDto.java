package com.project.wms.dto.requestdto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ClientRequestDto {
    private Long id;
    @NotEmpty(message = "Введите код")
    @Pattern(regexp = "\\d+", message = "Только цифры")
    private String codeClient; // код холодильника
    @NotEmpty(message = "Введите название тоговой точки")
    private String name; //название торговой точки
    @NotEmpty(message = "Введите адрес")
    private String address;
    @NotEmpty(message = "Введите ФИО")
    private String fio;
    @NotEmpty(message = "Введите номер телефона")
    @Pattern(regexp = "\\d+", message = "Только цифры")
    private String phone;
}
