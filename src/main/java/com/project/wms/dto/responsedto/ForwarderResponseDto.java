package com.project.wms.dto.responsedto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ForwarderResponseDto {
    private Long id;

    private String firstName;
    private String lastName;
    private String patronymic; //отчество
    private LocalDate birthDay;
    private String phone;

    public String getFullName(){
        return this.lastName + ' ' + this.firstName;
    }
}
