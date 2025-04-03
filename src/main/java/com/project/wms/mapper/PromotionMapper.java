package com.project.wms.mapper;

import com.project.wms.dto.responsedto.PromotionResponseDTO;
import com.project.wms.entity.PromotionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, PromotionProductMapper.class})
public interface PromotionMapper {


    @Mapping(source = "includedProducts", target = "includedProducts", qualifiedByName = "mapPromotionProducts")
    @Mapping(target = "isActive", expression = "java(mapIsActive(entity))")
    PromotionResponseDTO toResponseDto(PromotionEntity entity);

    List<PromotionResponseDTO> toResponseDto(List<PromotionEntity> entities);


    // Метод для вычисления значения isActive
    default Boolean mapIsActive(PromotionEntity entity) {
        LocalDate today = LocalDate.now();
        return !today.isBefore(entity.getStartDate()) && !today.isAfter(entity.getEndDate());
    }

}
