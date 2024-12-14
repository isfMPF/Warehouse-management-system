package com.project.wms.mapper;

import com.project.wms.dto.responsedto.OrderItemResponseDto;
import com.project.wms.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "order.id", target = "order")  // Преобразуем OrderEntity в его id
    @Mapping(source = "product.id", target = "product")  // Преобразуем ProductEntity в его id
    OrderItemResponseDto toResponseDto(OrderItemEntity orderItem);
}
