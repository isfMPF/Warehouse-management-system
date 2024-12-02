package com.project.wms.dto.responsedto;

import lombok.Data;

@Data
public class ClientResponseDto {

    private Long id;
    private Long codeClient; // код холодильника
    private String name; //название торговой точки
    private String address;
    private String fio;
    private String phone;
}
