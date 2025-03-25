package com.project.wms.mapper;


import com.project.wms.dto.responsedto.OrderItemResponseDto;
import com.project.wms.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    // Преобразование OrderItemEntity в OrderItemResponseDto
    @Mapping(source = "order.id", target = "order") // Преобразуем заказ в его ID
    @Mapping(source = "code.code", target = "code")
    @Mapping(source = "code.name", target = "name")
    @Mapping(source = "code.volume", target = "volume")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "total", target = "total")
    OrderItemResponseDto toResponseDto(OrderItemEntity item);
}
