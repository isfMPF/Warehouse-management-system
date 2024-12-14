package com.project.wms.service;

import com.project.wms.dto.requestdto.OrderItemRequestDto;
import com.project.wms.dto.requestdto.OrderRequestDto;
import com.project.wms.dto.responsedto.OrderResponseDto;
import com.project.wms.entity.ClientEntity;
import com.project.wms.entity.OrderEntity;
import com.project.wms.entity.OrderItemEntity;
import com.project.wms.entity.ProductEntity;
import com.project.wms.mapper.OrderMapper;
import com.project.wms.repository.ClientRepository;
import com.project.wms.repository.OrderItemRepository;
import com.project.wms.repository.OrderRepository;
import com.project.wms.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public OrderMapper orderMapper;
    @Autowired
    public ClientRepository clientRepository;
    @Autowired
    public ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;


    public void createOrder(OrderRequestDto orderRequestDto){

        ClientEntity client = clientRepository.findById(orderRequestDto.getCodeClient())
                .orElseThrow(() -> new RuntimeException("Client not found"));


        OrderEntity order = new OrderEntity();
        order.setCodeClient(client);
        order.setDate(LocalDate.now());
        order.setTotal(0.0);

        List<OrderItemEntity> itemEntities = new ArrayList<>();
        double totalOrderAmount = 0.0;

        for(OrderItemRequestDto orderItemRequestDto : orderRequestDto.getItem()){
            ProductEntity product = productRepository.findById(orderItemRequestDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Рассчитываем общую сумму для товара
            double totalItemAmount = orderItemRequestDto.getAmount() * product.getPrice();
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order); // Связываем товар с заказом
            orderItem.setProduct(product); // Связываем товар
            orderItem.setAmount(orderItemRequestDto.getAmount()); // Устанавливаем количество
            orderItem.setPrice(product.getPrice()); // Устанавливаем цену
            orderItem.setTotal(totalItemAmount); // Устанавливаем общую сумму для этого товара

            itemEntities.add(orderItem);
            totalOrderAmount += totalItemAmount;
        }

        order.setItem(itemEntities);
        order.setTotal(totalOrderAmount); // Устанавливаем общую сумму заказа
        orderRepository.save(order); // Сохраняем заказ

        //Сохраняем каждый товар
        orderItemRepository.saveAll(itemEntities);



    }

    // Метод для получения заказа по ID
    public OrderResponseDto getOrderById(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toResponseDto(order); // Преобразуем сущность в DTO
    }


}
