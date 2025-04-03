package com.project.wms.mapper;

import com.project.wms.dto.responsedto.PromotionProductResponseDto;
import com.project.wms.entity.PromotionProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface PromotionProductMapper {


    @Mapping(source = "promotion.id", target = "id")
    @Mapping(source = "product", target = "productResponseDto")
    PromotionProductResponseDto toResponseDto(PromotionProduct entity);

    List<PromotionProductResponseDto> toResponseDto(List<PromotionProduct> entities);

    @Named("mapPromotionProducts")
    default Set<PromotionProductResponseDto> mapPromotionProducts(Set<PromotionProduct> entities) {
        if (entities == null) {
            return Collections.emptySet();
        }
        return entities.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toSet());
    }
}
