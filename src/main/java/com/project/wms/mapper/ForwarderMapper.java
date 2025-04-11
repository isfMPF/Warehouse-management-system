package com.project.wms.mapper;

import com.project.wms.dto.requestdto.ForwarderRequestDto;
import com.project.wms.dto.responsedto.ForwarderResponseDto;
import com.project.wms.entity.ForwarderEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ForwarderMapper {

    ForwarderEntity toEntity(ForwarderRequestDto forwarderRequestDto);

    ForwarderResponseDto toResponseDto(ForwarderEntity forwarderEntity);
    List<ForwarderResponseDto> toResponseDto(List<ForwarderEntity> forwarderEntity);
}
