package com.project.wms.mapper;

import com.project.wms.dto.requestdto.TransportRequestDto;
import com.project.wms.dto.responsedto.TransportResponseDto;
import com.project.wms.entity.TransportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransportMapper {



    TransportEntity toRequestDto(TransportRequestDto transportRequestDto);


    TransportResponseDto toResponseDto(TransportEntity transportEntity);
}
