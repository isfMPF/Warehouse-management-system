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
import java.util.Optional;

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

        // Устанавливаем в заказ все элементы и общую сумму
        order.setItem(itemEntities);
        order.setTotal(totalOrderAmount);  // Устанавливаем общую сумму заказа

        // Сохраняем заказ в базе данных
        orderRepository.save(order);

        // Сохраняем каждый товар (элементы заказа) в базе данных
        orderItemRepository.saveAll(itemEntities);
    }

    @Transactional
    public void updateOrder(Long orderId, OrderRequestDto orderRequestDto) {
        // Получаем существующий заказ по ID
        OrderEntity existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ с таким ID не найден"));

        // Получаем клиента по ID из заказа
        ClientEntity client = clientRepository.findByCodeClient(orderRequestDto.getCodeClient());

        // Обновляем заказ
        existingOrder.setCodeClient(client);  // Обновляем клиента
        existingOrder.setDate(LocalDate.now());
        existingOrder.setTotal(0.0);  // Сбрасываем общую сумму для перерасчета

        // Список для элементов заказа (товары в заказе)
        List<OrderItemEntity> updatedItems = new ArrayList<>();
        double totalOrderAmount = 0.0;  // Перерасчет общей суммы заказа

        // Обрабатываем каждый товар из обновленного заказа
        for (OrderItemRequestDto orderItemRequestDto : orderRequestDto.getItems()) {

                // Получаем продукт по коду
                ProductEntity product = productRepository.findByCode(String.valueOf(orderItemRequestDto.getCode()));
            System.out.println("orderItemRequestDto = " + orderItemRequestDto);
                // Обновляем количество товара на складе
                int remainingAmount = product.getAmount() - orderItemRequestDto.getAmount();
                if (remainingAmount < 0) {
                    throw new IllegalArgumentException("Недостаточно товара на складе");
                }
                product.setAmount(remainingAmount);
                productRepository.save(product);

                // Рассчитываем общую сумму для товара
                double totalItemAmount = orderItemRequestDto.getAmount() * product.getPrice();

                // Проверяем, существует ли уже этот товар в заказе
                Optional<OrderItemEntity> existingItemOpt = existingOrder.getItem().stream()
                        .filter(item -> item.getCode().getCode().equals(orderItemRequestDto.getCode()))
                        .findFirst();

                OrderItemEntity orderItem;
                if (existingItemOpt.isPresent()) {
                    // Обновляем существующий товар в заказе
                    orderItem = existingItemOpt.get();
                    orderItem.setAmount(orderItemRequestDto.getAmount());
                    orderItem.setPrice(product.getPrice());
                    orderItem.setTotal(totalItemAmount);
                } else {
                    // Создаем новый товар в заказе
                    orderItem = new OrderItemEntity();
                    orderItem.setOrder(existingOrder);  // Связываем с заказом
                    orderItem.setCode(product);  // Связываем с продуктом
                    orderItem.setAmount(orderItemRequestDto.getAmount());  // Устанавливаем количество
                    orderItem.setPrice(product.getPrice());  // Устанавливаем цену
                    orderItem.setTotal(totalItemAmount);  // Устанавливаем общую сумму
                }

                // Добавляем товар в список
                updatedItems.add(orderItem);

                // Добавляем сумму товара в общую сумму заказа
                totalOrderAmount += totalItemAmount;

        }

        // Устанавливаем в заказ обновленные элементы и общую сумму
        existingOrder.setItem(updatedItems);
        existingOrder.setTotal(totalOrderAmount);  // Обновляем общую сумму заказа

        // Сохраняем заказ в базе данных
        orderRepository.save(existingOrder);

        // Сохраняем или обновляем каждый товар (элементы заказа) в базе данных
        orderItemRepository.saveAll(updatedItems);
    }


    public List<OrderEntity> getAllOrders(){
        return orderRepository.findAll();
    }

    public OrderEntity getOrderById(Long id){
        return orderRepository.findOrderById(id);
    }


    @Transactional
    public void deleteOrderWithItems(Long orderId) {
        // Находим заказ по id
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Получаем список товаров из заказа
        List<OrderItemEntity> orderItems = order.getItem();

        // Увеличиваем количество товаров на складе
        for (OrderItemEntity item : orderItems) {
            // Находим товар по коду

            ProductEntity product = productRepository.findByCode(String.valueOf(item.getCode().getCode()));

            // Увеличиваем количество товара на складе
            product.setAmount(product.getAmount() + item.getAmount());
            productRepository.save(product); // Сохраняем обновленное количество
        }

        // Удаляем все связанные элементы
        orderItemRepository.deleteAll(orderItems);

        // Удаляем сам заказ
        orderRepository.delete(order);
    }


}
