package com.project.wms.repository;

import com.project.wms.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity,Long> {

    // Найти все элементы по ID заказа
    List<OrderItemEntity> findAllByOrderId(Long orderId);

    @Modifying
    @Query("DELETE FROM OrderItemEntity oi WHERE oi.order.id = :orderId AND oi.code.code = :productCode")
    void deleteByOrderIdAndProductCode(@Param("orderId") Long orderId, @Param("productCode") String productCode);


    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.order.id = :orderId AND oi.code.code = :productCode")
    Optional<OrderItemEntity> findByOrderIdAndProductCode(@Param("orderId") Long orderId, @Param("productCode") String productCode);
}
