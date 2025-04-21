package com.project.wms.dto.responsedto;

import lombok.Data;

import java.time.DayOfWeek;

@Data
public class ClientResponseDto {

    private Long id;
    private Long codeClient; // код холодильника
    private String name; //название торговой точки
    private String address;
    private String fio;
    private String phone;
    private DayOfWeek dayOfWeek;
}
