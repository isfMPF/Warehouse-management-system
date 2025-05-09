package com.project.wms.service;

import com.project.wms.entity.OrderItemEntity;
import com.project.wms.mapper.OrderItemMapper;
import com.project.wms.repository.OrderItemRepository;
import com.project.wms.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderItemEntity> getAllItemsByOrderId(long orderId){
        return orderItemRepository.findAllByOrderId(orderId);
    }


}
