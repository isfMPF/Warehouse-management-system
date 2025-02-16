package com.project.wms.repository;

import com.project.wms.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity,Long> {

    // Найти все элементы по ID заказа
    List<OrderItemEntity> findAllByOrderId(Long orderId);
}
