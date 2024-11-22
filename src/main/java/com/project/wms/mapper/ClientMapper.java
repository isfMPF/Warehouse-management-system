package com.project.wms.mapper;

import com.project.wms.dto.requestdto.ClientRequestDto;
import com.project.wms.dto.responsedto.ClientResponseDto;
import com.project.wms.entity.ClientEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientEntity toEntity(ClientRequestDto requestDto);
    ClientResponseDto toResponseDto(ClientEntity client);


}
