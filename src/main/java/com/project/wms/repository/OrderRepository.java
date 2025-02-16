package com.project.wms.repository;

import com.project.wms.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    OrderEntity findOrderById(Long id);
}
