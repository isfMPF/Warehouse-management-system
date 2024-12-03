package com.project.wms.mapper;

import com.project.wms.dto.requestdto.ProductRequestDto;
import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", source = "id")
    ProductEntity toEntity(ProductRequestDto productRequestDto);
    ProductResponseDto toResponseDto(ProductEntity product);
}
