package com.project.wms.dto.responsedto;

import lombok.Data;

@Data
public class TransportResponseDto {
    private Long id;
    private String name;
    private String number;
    private Double capacity;

    public String getInfo(){
        return this.name + " TJ " + this.number;
    }
}
