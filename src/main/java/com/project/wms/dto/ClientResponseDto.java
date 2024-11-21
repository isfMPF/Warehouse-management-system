package com.project.wms.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ClientResponseDto {

    private Long codeClient; // код холодильника
    private String name; //название торговой точки
    private String address;
    private String fio;
    private String phone;
}
