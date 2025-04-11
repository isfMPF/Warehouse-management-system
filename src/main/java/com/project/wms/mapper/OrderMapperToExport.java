package com.project.wms.mapper;

import com.project.wms.dto.responsedto.OrderResponseDto;
import com.project.wms.dto.responsedto.OrderResponseDtoToExport;
import com.project.wms.entity.ClientEntity;
import com.project.wms.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapperToExport {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "codeClient", target = "codeClient")  // Сохраняем код клиента
    @Mapping(source = "codeClient.name", target = "clientName") // Маппируем имя клиента
    @Mapping(source = "total", target = "total")
    @Mapping(source = "item", target = "item")  // Заменяем 'items' на 'item', чтобы соответствовать полю в OrderEntity
    @Mapping(target = "forwarder", ignore = true)
    @Mapping(target = "transport", ignore = true)
    OrderResponseDtoToExport toResponseDto(OrderEntity order);

    // Метод для маппинга ClientEntity в Long (код клиента)
    default Long mapClientToCode(ClientEntity clientEntity) {
        return clientEntity != null ? clientEntity.getCodeClient() : null;
    }
}
