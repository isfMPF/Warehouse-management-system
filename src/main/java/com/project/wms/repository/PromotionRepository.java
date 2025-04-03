package com.project.wms.repository;

import com.project.wms.entity.PromotionEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {

    @EntityGraph(attributePaths = {"freeProduct", "requiredProduct", "includedProducts.product"})
    @Query("SELECT p FROM PromotionEntity p ORDER BY p.startDate DESC")
    List<PromotionEntity> findAllWithProductsOrderedByStartDate();


    @EntityGraph(attributePaths = {"freeProduct", "requiredProduct"})
    List<PromotionEntity> findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
            LocalDate startDate, LocalDate endDate
    );

    default List<PromotionEntity> findActivePromotions() {
        LocalDate today = LocalDate.now();
        return findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(today, today);
    }
}

