package com.project.wms.repository;

import com.project.wms.entity.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {

    // Добавляем новый метод для получения всех акций
    @Query("SELECT p FROM PromotionEntity p LEFT JOIN FETCH p.freeProduct LEFT JOIN FETCH p.requiredProduct LEFT JOIN FETCH p.includedProducts ORDER BY p.startDate DESC")
    List<PromotionEntity> findAllWithProducts();
}
