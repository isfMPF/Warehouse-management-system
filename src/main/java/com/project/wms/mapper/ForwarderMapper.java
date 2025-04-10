package com.project.wms.mapper;

import com.project.wms.dto.requestdto.ForwarderRequestDto;
import com.project.wms.dto.responsedto.ForwarderResponseDto;
import com.project.wms.entity.ForwarderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ForwarderMapper {

    ForwarderEntity toRequestDto(ForwarderRequestDto forwarderRequestDto);

    ForwarderResponseDto toResponseDto(ForwarderEntity forwarderEntity);
}
