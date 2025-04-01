package com.project.wms.service;

import com.project.wms.dto.requestdto.OrderItemRequestDto;
import com.project.wms.dto.requestdto.OrderRequestDto;
import com.project.wms.dto.responsedto.OrderItemResponseDto;
import com.project.wms.dto.responsedto.OrderResponseDto;
import com.project.wms.dto.responsedto.PromotionResponseDTO;
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
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public ClientRepository clientRepository;
    @Autowired
    public ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private PromotionService promotionService;
    @Autowired
    private OrderMapper orderMapper;

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
                orderItem.setWeight(product.getWeight());
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




    public List<OrderEntity> getAllOrders(){
        return orderRepository.findAll();
    }

    public OrderEntity getOrderById(Long id){
        return orderRepository.findOrderById(id);
    }

    public List<OrderEntity> getOrdersBetweenDates(LocalDate startDate, LocalDate endDate) {
        return orderRepository.findByDateBetween(startDate, endDate);
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

    public void calculatePromo(Long orderId){

        OrderResponseDto order = orderMapper.toResponseDto(orderRepository.findOrderById(orderId));

        // Получаем список товаров из заказа
        List<OrderItemResponseDto> orderItems = order.getItem();

        List<PromotionResponseDTO> activePromo = promotionService.getActivePromo();
        List<OrderItemEntity> freeItemsToAdd = new ArrayList<>();

        if(!activePromo.isEmpty()) {
            for (PromotionResponseDTO promo : activePromo) {

                // Приводим коды акции к Long для сравнения
                Set<Long> promoIncludedCodes = promo.getIncludedProductCodes().stream()
                        .map(Long::valueOf)
                        .collect(Collectors.toSet());

                // 1. Проверяем наличие обязательного товара
                boolean hasRequiredProduct = orderItems.stream()
                        .anyMatch(item -> item.getCode().toString().equals(promo.getRequiredProductCode()));


                if (!hasRequiredProduct) {
                    continue;
                }

                // 2. Считаем общее количество товаров акции в заказе
                int totalIncludedProducts = orderItems.stream()
                        .filter(item -> promoIncludedCodes.contains(item.getCode()))
                        .mapToInt(OrderItemResponseDto::getAmount)
                        .sum();


                // 3. Проверяем условие акции и рассчитываем количество бесплатных товаров
                if (totalIncludedProducts >= promo.getRequiredQuantity() - 1) {
                    // Вычисляем сколько раз выполнено условие акции (целочисленное деление)
                    int freeItemsCount = (totalIncludedProducts / (promo.getRequiredQuantity() - 1)) * promo.getFreeQuantity();
                    System.out.println("Подарок:" + freeItemsCount);
                    // 4. Добавляем бесплатный товар
                    ProductEntity freeProduct = productRepository.findByCode(promo.getFreeProductCode());
                    System.out.println("freeProduct: " + freeProduct.toString());
                    // Проверяем, не добавлен ли уже этот товар как бесплатный
                    boolean alreadyAdded = orderItems.stream()
                            .anyMatch(item -> item.getCode().toString().equals(promo.getFreeProductCode())
                                    && item.getPrice().compareTo(BigDecimal.ZERO.doubleValue()) == 0);

                    if (!alreadyAdded && freeItemsCount > 0) {
                        OrderItemEntity freeItem = new OrderItemEntity();
                        freeItem.setOrder(orderRepository.findById(orderId).orElseThrow());
                        freeItem.setCode(freeProduct);
                        freeItem.setAmount(freeItemsCount);
                        freeItem.setPrice(0.0);
                        freeItem.setTotal(0.0);
                        freeItem.setWeight(freeProduct.getWeight());

                        freeItemsToAdd.add(freeItem);
                    }
                }
            }
        }

        // 5. Сохраняем все бесплатные товары
        if (!freeItemsToAdd.isEmpty()) {
            orderItemRepository.saveAll(freeItemsToAdd);
        }
    }

    public void returnOrder(Long id){
        deleteOrderWithItems(id);
    }
}
