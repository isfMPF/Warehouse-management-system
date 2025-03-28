package com.project.wms.service;

import com.project.wms.dto.requestdto.PromotionRequestDTO;
import com.project.wms.dto.responsedto.PromotionResponseDTO;
import com.project.wms.entity.ProductEntity;
import com.project.wms.entity.PromotionEntity;
import com.project.wms.mapper.PromotionMapper;
import com.project.wms.repository.ProductRepository;
import com.project.wms.repository.PromotionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private  ProductRepository productRepository;
    @Autowired
    private PromotionMapper promotionMapper;

   public void createPromo(PromotionRequestDTO promotionRequestDTO){

       // 1. Проверяем даты акции
       validatePromotionDates(promotionRequestDTO.getStartDate(), promotionRequestDTO.getEndDate());

       // 2. Находим связанные продукты
       ProductEntity freeProduct = productRepository.findByCode(promotionRequestDTO.getFreeProductCode());


       ProductEntity requiredProduct = productRepository.findByCode(promotionRequestDTO.getRequiredProductCode());

       // 3. Находим все включенные продукты
       Set<ProductEntity> includedProducts = productRepository.findByCodeIn(promotionRequestDTO.getIncludedProductCodes());

       // 4. Проверяем, что все указанные продукты найдены
       if (includedProducts.size() != promotionRequestDTO.getIncludedProductCodes().size()) {
           Set<String> foundCodes = includedProducts.stream()
                   .map(ProductEntity::getCode)
                   .collect(Collectors.toSet());

           Set<String> notFoundCodes = promotionRequestDTO.getIncludedProductCodes().stream()
                   .filter(code -> !foundCodes.contains(code))
                   .collect(Collectors.toSet());

           throw new EntityNotFoundException(
                   "Следующие продукты не найдены: " + String.join(", ", notFoundCodes));
       }

       // 5. Преобразуем DTO в Entity
       PromotionEntity promotion = promotionMapper.toEntity(promotionRequestDTO);

       // 6. Устанавливаем связи
       promotion.setFreeProduct(freeProduct);
       promotion.setRequiredProduct(requiredProduct);
       promotion.setIncludedProducts(includedProducts);

       // 7. Сохраняем в базу данных
       promotionRepository.save(promotion);
   }

    private void validatePromotionDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Дата начала акции не может быть позже даты окончания");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Дата начала акции не может быть в прошлом");
        }
    }

    public List<PromotionResponseDTO> getAllPromotions() {
        List<PromotionEntity> promotions = promotionRepository.findAllWithProducts();
        return promotions.stream()
                .map(promotionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    public List<PromotionResponseDTO> getActivePromo(){
       List<PromotionResponseDTO> list = promotionRepository.findActivePromo().stream()
               .map(promotionMapper::toResponseDTO)
               .toList();

       return list;
    }
}
