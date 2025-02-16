package com.project.wms.controller;

import com.project.wms.dto.requestdto.OrderRequestDto;
import com.project.wms.dto.responsedto.ClientResponseDto;
import com.project.wms.dto.responsedto.OrderItemResponseDto;
import com.project.wms.dto.responsedto.OrderResponseDto;
import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.dto.requestdto.OrderItemRequestDto;
import com.project.wms.entity.OrderEntity;
import com.project.wms.mapper.ClientMapper;
import com.project.wms.mapper.OrderItemMapper;
import com.project.wms.mapper.OrderMapper;
import com.project.wms.mapper.ProductMapper;
import com.project.wms.service.ClientService;
import com.project.wms.service.OrderItemService;
import com.project.wms.service.OrderService;
import com.project.wms.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public String showAllOrders(Model model) {
        List<OrderResponseDto> orders = orderService.getAllOrders().stream()
                .filter(orderEntity -> Objects.equals(orderEntity.getDate(), LocalDate.now()))
                .map(orderMapper::toResponseDto)  // Используем OrderMapper для преобразования заказа в DTO
                .collect(Collectors.toList());
        model.addAttribute("orders", orders);

        // Передаем список заказов, включая элементы заказа
        return "/order/viewOrders";  // Шаблон, где отображаются заказы
    }



    @GetMapping("/search")
    public String searchBetween(
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate start, @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate end, Model model)
    {

        List<OrderResponseDto> orders = orderService.getAllOrders().stream()
                .filter(orderEntity -> {
                    if (start != null && end != null) {
                        return !orderEntity.getDate().isBefore(start) && !orderEntity.getDate().isAfter(end);
                    } else if (start != null) {
                        return !orderEntity.getDate().isBefore(start);
                    } else if (end != null) {
                        return !orderEntity.getDate().isAfter(end);
                    } else {
                        return false; // Если даты не указаны, возвращаем все заказы
                    }
                })
                .map(orderMapper::toResponseDto)
                .toList();

        model.addAttribute("orders", orders);

        return "/order/viewOrders";
    }

    @GetMapping("/orderDetails/{id}")
    public String showDetailsOrder(@PathVariable(value = "id") long id, Model model) {

        OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(id));
        System.out.println(order);
        model.addAttribute("orders", order);


        return "/order/detailsOrder";
    }

    @GetMapping("/create")
    public String createOrder(Model model){

        List<ClientResponseDto> clientsToSelect = StreamSupport.stream(clientService.getAllClients().spliterator(), false)
                .map(clientMapper::toResponseDto)
                .toList();

        model.addAttribute("clients",clientsToSelect);

        List<ProductResponseDto> productsToSelect = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                .map(productMapper::toResponseDto)
                .toList();

        model.addAttribute("products",productsToSelect);

        model.addAttribute("orderRequestDto",new OrderRequestDto());
        return "/order/viewItemsOrder";
    }



    @PostMapping("/create")
    public String addToCart(@Valid @ModelAttribute("orderRequestDto") OrderRequestDto orderRequestDto,
                            BindingResult errors, Model model){

        // Проверка, что выбран хотя бы один товар
        boolean atLeastOneSelected = orderRequestDto.getItems().stream()
                .anyMatch(OrderItemRequestDto::isSelected);

        if (!atLeastOneSelected) {
            errors.reject("items", "Выберите хотя бы один товар"); // Добавляем глобальную ошибку
        }

        // Проверка на ошибки валидации
        if(errors.hasErrors()){
            List<ProductResponseDto> productsToSelect = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                    .map(productMapper::toResponseDto)
                    .toList();

            model.addAttribute("products", productsToSelect);

            List<ClientResponseDto> clientsToSelect = StreamSupport.stream(clientService.getAllClients().spliterator(), false)
                    .map(clientMapper::toResponseDto)
                    .toList();

            model.addAttribute("clients",clientsToSelect);

            return "/order/viewItemsOrder";
        }

         orderService.createOrder(orderRequestDto);

        return "redirect:/orders";
    }

    @GetMapping("/edit-order/{id}")
    public String editOrder(@PathVariable(value = "id") Long id, Model model){

        OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(id));
        order.getItems().forEach(item -> {
            if (item.getAmount() > 0) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }
        });
        List<ProductResponseDto> productsToSelect = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                .map(productMapper::toResponseDto)
                .toList();


        System.out.println("Заказ" + " " + order);
        System.out.println("Продукты" + " " + productsToSelect);
        model.addAttribute("products",productsToSelect);
        model.addAttribute("order", order);
        model.addAttribute("orderRequestDto", new OrderRequestDto());

        return "/order/editOrder";
    }

    @PostMapping("/edit-order")
    public String orderEdit(@ModelAttribute("order") @Valid OrderRequestDto orderRequestDto, BindingResult errors,
                            Model model, @RequestParam("orderId") Long orderId){
        // Проверка, что выбран хотя бы один товар
        boolean atLeastOneSelected = orderRequestDto.getItems().stream()
                .anyMatch(OrderItemRequestDto::isSelected);

        if (!atLeastOneSelected) {
            errors.reject("items", "Выберите хотя бы один товар"); // Добавляем глобальную ошибку
        }

        // Проверка на ошибки валидации
        if(errors.hasErrors()){
            System.out.println("Ошибка" + " " + errors);
            OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(orderId));
            order.getItems().forEach(item -> {
                if (item.getAmount() > 0) {
                    item.setSelected(true);
                } else {
                    item.setSelected(false);
                }
            });
            List<ProductResponseDto> productsToSelect = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                    .map(productMapper::toResponseDto)
                    .toList();


            model.addAttribute("products",productsToSelect);
            model.addAttribute("order", order);

            return "/order/editOrder";
        }

        orderService.updateOrder(orderId, orderRequestDto);

        return "redirect:/orders";
    }



}
