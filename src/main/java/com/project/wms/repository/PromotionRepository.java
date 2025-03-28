package com.project.wms.repository;

import com.project.wms.entity.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {

    // Добавляем новый метод для получения всех акций
    @Query("SELECT p FROM PromotionEntity p LEFT JOIN FETCH p.freeProduct LEFT JOIN FETCH p.requiredProduct LEFT JOIN FETCH p.includedProducts ORDER BY p.startDate DESC")
    List<PromotionEntity> findAllWithProducts();

    @Query("SELECT DISTINCT p FROM PromotionEntity p " +
            "LEFT JOIN FETCH p.freeProduct " +
            "LEFT JOIN FETCH p.requiredProduct " +
            "LEFT JOIN FETCH p.includedProducts " +
            "WHERE CURRENT_DATE BETWEEN p.startDate AND p.endDate")
    List<PromotionEntity> findActivePromo();

    Optional<PromotionEntity> findById(Long id);
}
