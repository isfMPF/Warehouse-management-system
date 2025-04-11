package com.project.wms.mapper;

import com.project.wms.dto.requestdto.TransportRequestDto;
import com.project.wms.dto.responsedto.TransportResponseDto;
import com.project.wms.entity.TransportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface TransportMapper {



    TransportEntity toEntity(TransportRequestDto transportRequestDto);


   TransportResponseDto toResponseDto(TransportEntity transportEntity);

    List<TransportResponseDto> toResponseDTO(List<TransportEntity> transportEntity);


}
