package com.project.wms.service;

import com.project.wms.entity.OrderItemEntity;
import com.project.wms.entity.ProductEntity;
import com.project.wms.mapper.OrderItemMapper;
import com.project.wms.repository.OrderItemRepository;
import com.project.wms.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductRepository productRepository;

    public List<OrderItemEntity> getAllItemsByOrderId(long orderId){
        return orderItemRepository.findAllByOrderId(orderId);
    }

    @Transactional
    public void removeItemFromOrder(Long orderId, String productCode) {
        // Находим товар в заказе
        OrderItemEntity orderItem = orderItemRepository.findByOrderIdAndProductCode(orderId, productCode)
                .orElseThrow(() -> new RuntimeException("Товар не найден в заказе"));

        // Находим товар на складе
        ProductEntity product = productRepository.findByCode(productCode);

        // Увеличиваем количество товара на складе
        product.setAmount(product.getAmount() + orderItem.getAmount());

        // Сохраняем обновленное количество товара на складе
        productRepository.save(product);

        // Удаляем товар из заказа
        orderItemRepository.deleteByOrderIdAndProductCode(orderId, productCode);
    }




}
