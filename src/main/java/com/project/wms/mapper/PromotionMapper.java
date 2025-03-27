package com.project.wms.mapper;

import com.project.wms.dto.requestdto.PromotionRequestDTO;
import com.project.wms.dto.responsedto.PromotionResponseDTO;
import com.project.wms.entity.ProductEntity;
import com.project.wms.entity.PromotionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    @Mapping(target = "isActive", expression = "java(entity.isActive())")
    @Mapping(target = "freeProductCode", source = "freeProduct.code")
    @Mapping(target = "freeProductVolume", source = "freeProduct.volume")
    @Mapping(target = "freeProductName", source = "freeProduct.name")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "requiredProductCode", source = "requiredProduct.code")
    @Mapping(target = "requiredProductVolume", source = "requiredProduct.volume")
    @Mapping(target = "requiredProductName", source = "requiredProduct.name")
    @Mapping(target = "includedProductCodes", source = "includedProducts", qualifiedByName = "mapProductCodes")
    @Mapping(target = "includedProductNames", source = "includedProducts", qualifiedByName = "mapProductNames")
    @Mapping(target = "includedProductVolumes", source = "includedProducts", qualifiedByName = "mapProductVolume")
    PromotionResponseDTO toResponseDTO(PromotionEntity entity);

    @Mapping(target = "freeProduct", ignore = true)
    @Mapping(target = "requiredProduct", ignore = true)
    @Mapping(target = "includedProducts", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    PromotionEntity toEntity(PromotionRequestDTO dto);

    @Named("mapProductCodes")
    default Set<String> mapProductCodes(Set<ProductEntity> products) {
        return products.stream()
                .map(ProductEntity::getCode)
                .collect(Collectors.toSet());
    }

    @Named("mapProductNames")
    default Set<String> mapProductNames(Set<ProductEntity> products) {
        return products.stream()
                .map(ProductEntity::getName)
                .collect(Collectors.toSet());
    }

    @Named("mapProductVolume")
    default Set<String> mapProductVolume(Set<ProductEntity> products) {
        return products.stream()
                .map(ProductEntity::getVolume)
                .collect(Collectors.toSet());
    }
}