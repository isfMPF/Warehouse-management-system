package com.project.wms.mapper;

import com.project.wms.dto.requestdto.ClientRequestDto;
import com.project.wms.dto.responsedto.ClientResponseDto;
import com.project.wms.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", source = "id")
    ClientEntity toEntity(ClientRequestDto requestDto);
    ClientResponseDto toResponseDto(ClientEntity client);


}
