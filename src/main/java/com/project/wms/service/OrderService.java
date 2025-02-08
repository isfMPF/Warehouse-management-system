package com.project.wms.service;

import com.project.wms.dto.requestdto.OrderItemRequestDto;
import com.project.wms.dto.requestdto.OrderRequestDto;
import com.project.wms.entity.ClientEntity;
import com.project.wms.entity.OrderEntity;
import com.project.wms.entity.OrderItemEntity;
import com.project.wms.entity.ProductEntity;
import com.project.wms.mapper.ClientMapper;
import com.project.wms.mapper.OrderMapper;
import com.project.wms.mapper.ProductMapper;
import com.project.wms.repository.ClientRepository;
import com.project.wms.repository.OrderItemRepository;
import com.project.wms.repository.OrderRepository;
import com.project.wms.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ClientMapper clientMapper;

    @Transactional
    public void createOrder(OrderRequestDto orderRequestDto) {

        // Получаем клиента по ID
        ClientEntity client = clientRepository.findByCodeClient(orderRequestDto.getCodeClient());
        // Создаем новый объект заказа
        OrderEntity order = new OrderEntity();
        order.setCodeClient(client);  // Устанавливаем клиента в заказ
        order.setDate(LocalDate.now());  // Устанавливаем текущую дату
        order.setTotal(0.0);  // Изначальная сумма заказа

        // Список для элементов заказа
        List<OrderItemEntity> itemEntities = new ArrayList<>();
        double totalOrderAmount = 0.0;  // Общая сумма всех товаров

        // Обрабатываем каждый товар из заказа
        for (OrderItemRequestDto orderItemRequestDto : orderRequestDto.getItems()) {

            // Проверяем, был ли выбран товар (если selected == true)
            if (orderItemRequestDto.isSelected()) {
                // Получаем продукт по ID
                ProductEntity product = (ProductEntity) productRepository.findByCode(String.valueOf(orderItemRequestDto.getCode()));

                product.setAmount(product.getAmount() - orderItemRequestDto.getAmount());
                productRepository.save(product);


                // Рассчитываем общую сумму для товара
                double totalItemAmount = orderItemRequestDto.getAmount() * product.getPrice();
                OrderItemEntity orderItem = new OrderItemEntity();
                orderItem.setOrder(order);  // Связываем товар с заказом
                orderItem.setCode(product);  // Связываем товар с продуктом
                orderItem.setAmount(orderItemRequestDto.getAmount());  // Устанавливаем количество
                orderItem.setPrice(product.getPrice());  // Устанавливаем цену
                orderItem.setTotal(totalItemAmount);  // Устанавливаем общую сумму для этого товара

                // Добавляем элемент в список
                itemEntities.add(orderItem);

                // Добавляем сумму товара в общую сумму заказа
                totalOrderAmount += totalItemAmount;
            }
        }

        // Устанавливаем в заказ все элементы и общую сумму
        order.setItem(itemEntities);
        order.setTotal(totalOrderAmount);  // Устанавливаем общую сумму заказа

        // Сохраняем заказ в базе данных
        orderRepository.save(order);

        // Сохраняем каждый товар (элементы заказа) в базе данных
        orderItemRepository.saveAll(itemEntities);
    }


    public List<OrderEntity> getAllOrders(){
        return orderRepository.findAll();
    }

    public List<OrderEntity> getOrderById(Long id){
        return orderRepository.findOrderById(id);
    }

}
