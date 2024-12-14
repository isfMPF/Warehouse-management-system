package com.project.wms.mapper;

import com.project.wms.dto.responsedto.OrderResponseDto;
import com.project.wms.entity.ClientEntity;
import com.project.wms.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "id", target = "id")  // Преобразование id из Long в Long (если типы не совпадают)
    @Mapping(source = "codeClient", target = "codeClient")  // Преобразуем объект ClientEntity в Long
    @Mapping(source = "item", target = "item")  // Преобразование списка OrderItemEntity в OrderItemDto (понадобится отдельный маппер для OrderItem)
    @Mapping(source = "total", target = "total")  // Преобразуем total напрямую
    OrderResponseDto toResponseDto(OrderEntity order);

    // Метод для маппинга ClientEntity в Long (код клиента)
    default Long mapClientToCode(ClientEntity client) {
        return client != null ? client.getCodeClient() : null;  // возвращаем Long, представляющий код клиента
    }
}
