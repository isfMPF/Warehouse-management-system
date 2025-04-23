package com.project.wms.service;

import com.project.wms.dto.requestdto.PromotionRequestDTO;
import com.project.wms.dto.responsedto.PromotionResponseDTO;
import com.project.wms.entity.ProductEntity;
import com.project.wms.entity.PromotionEntity;
import com.project.wms.mapper.PromotionMapper;
import com.project.wms.repository.ProductRepository;
import com.project.wms.repository.PromotionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;
    private final PromotionMapper promotionMapper;

    public void createPromo(PromotionRequestDTO promotionRequestDTO) {
        // 1. Проверяем даты акции
        validatePromotionDates(promotionRequestDTO.getStartDate(), promotionRequestDTO.getEndDate());

        // 2. Находим и валидируем бесплатный товар
        ProductEntity freeProduct = productRepository.findByCode(promotionRequestDTO.getFreeProduct().getCode());

        // 3. Находим и валидируем обязательный товар
        ProductEntity requiredProduct = productRepository.findByCode(promotionRequestDTO.getRequiredProduct().getCode());

        // 4. Собираем коды включенных товаров
        Set<String> includedProductCodes = promotionRequestDTO.getIncludedProducts();

        // 5. Находим все включенные продукты
        Set<ProductEntity> includedProducts = productRepository.findByCodeIn(includedProductCodes);

        // 6. Проверяем, что все указанные продукты найдены
        validateProductsExist(includedProductCodes, includedProducts);

        // 7. Создаем и настраиваем сущность акции
        PromotionEntity promotion = new PromotionEntity();
        promotion.setName(promotionRequestDTO.getName());
        promotion.setStartDate(promotionRequestDTO.getStartDate());
        promotion.setEndDate(promotionRequestDTO.getEndDate());
        promotion.setRequiredQuantity(promotionRequestDTO.getRequiredQuantity());
        promotion.setFreeQuantity(promotionRequestDTO.getFreeQuantity());
        promotion.setFreeProduct(freeProduct);
        promotion.setRequiredProduct(requiredProduct);

        // 8. Добавляем включенные товары
        includedProducts.forEach(promotion::addIncludedProduct);

        // 9. Сохраняем акцию
        PromotionEntity savedPromotion = promotionRepository.save(promotion);

    }

    private void validateProductsExist(Set<String> requestedCodes, Set<ProductEntity> foundProducts) {
        if (foundProducts.size() != requestedCodes.size()) {
            Set<String> foundCodes = foundProducts.stream()
                    .map(product -> product.getCode())
                    .collect(Collectors.toSet());

            Set<String> notFoundCodes = requestedCodes.stream()
                    .filter(code -> !foundCodes.contains(code))
                    .collect(Collectors.toSet());

            throw new EntityNotFoundException(
                    "Следующие товары не найдены: " + String.join(", ", notFoundCodes));
        }
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
        return promotionRepository.findAllWithProductsOrderedByStartDate().stream()
                .map(promotionMapper::toResponseDto)
                .collect(Collectors.toList());
    }



    public List<PromotionResponseDTO> getActivePromotions() {
        return promotionRepository.findActivePromotions().stream()
                .map(promotionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<PromotionResponseDTO> getActivePromoById(int id) {
        LocalDate today = LocalDate.now();

        return promotionRepository.findById((long) id)
                .filter(promo -> !promo.getStartDate().isAfter(today)) // дата начала не позже сегодня
                .filter(promo -> !promo.getEndDate().isBefore(today))  // дата окончания не раньше сегодня
                .map(promotionMapper::toResponseDto);
    }


    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

}
