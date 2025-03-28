package com.project.wms.mapper;

import com.project.wms.dto.requestdto.ProductRequestDto;
import com.project.wms.dto.responsedto.OrderItemResponseDto;
import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", source = "id")
    ProductEntity toEntity(ProductRequestDto productRequestDto);
    ProductResponseDto toResponseDto(ProductEntity product);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "volume", target = "volume")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "amount", target = "amount")
    @Mapping(target = "unit", ignore = true) // Поле unit не заполняется
    @Mapping(source = "weight", target = "weight")
    ProductResponseDto toProductResponseDto(OrderItemResponseDto orderItem);

    List<ProductResponseDto> toProductResponseDto(List<OrderItemResponseDto> item);
}
