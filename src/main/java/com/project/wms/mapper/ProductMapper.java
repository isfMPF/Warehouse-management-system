package com.project.wms.mapper;

import com.project.wms.dto.requestdto.ProductRequestDto;
import com.project.wms.dto.responsedto.OrderItemResponseDto;
import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.entity.ProductEntity;
import com.project.wms.util.cart.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "version", ignore = true) // Игнорируем version при создании новой сущности
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


    ProductRequestDto toRequestDto(ProductEntity entity);


    List<ProductResponseDto> toProductResponseDto(List<OrderItemResponseDto> item);

    @Mapping(target = "product", source = ".", qualifiedByName = "orderItemToProductDto")
    @Mapping(target = "amount", source = "amount")
    CartItem toCartItem(OrderItemResponseDto orderItemDto);

    List<CartItem> toCart(List<OrderItemResponseDto> items);

    @Named("orderItemToProductDto")
    default ProductResponseDto orderItemToProductDto(OrderItemResponseDto orderItemDto) {
        if (orderItemDto == null) {
            return null;
        }
        ProductResponseDto productDto = new ProductResponseDto();
        productDto.setId(orderItemDto.getId());
        productDto.setCode(String.valueOf(orderItemDto.getCode()));
        productDto.setName(orderItemDto.getName());
        productDto.setVolume(orderItemDto.getVolume());
        productDto.setQuantity(orderItemDto.getQuantity());
        productDto.setPrice(orderItemDto.getPrice());
        productDto.setAmount(orderItemDto.getAmount());
        productDto.setWeight(orderItemDto.getWeight());
        productDto.setUnit("л");
        return productDto;
    }
}
