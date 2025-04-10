package com.project.wms.dto.requestdto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ForwarderRequestDto {


    @NotEmpty(message = "Введите имя")
    private String firstName;
    @NotEmpty(message = "Введите фамилию")
    private String lastName;
    @NotEmpty(message = "Введите отчество")
    private String patronymic; //отчество
    @NotNull(message = "Дата рождения не может быть пустой")
    private LocalDate birthDay;
    @NotEmpty(message = "Введите телефон")
    private String phone;
}
