package com.project.wms.controller;

import com.project.wms.dto.requestdto.OrderRequestDto;
import com.project.wms.dto.responsedto.ClientResponseDto;
import com.project.wms.dto.responsedto.OrderResponseDto;
import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.mapper.ClientMapper;
import com.project.wms.mapper.OrderItemMapper;
import com.project.wms.mapper.OrderMapper;
import com.project.wms.mapper.ProductMapper;
import com.project.wms.service.ClientService;
import com.project.wms.service.OrderItemService;
import com.project.wms.service.OrderService;
import com.project.wms.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
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

        return "/order/viewOrders";
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
    public String createOrder(Model model, HttpSession session){

        List<ClientResponseDto> clientsToSelect = StreamSupport.stream(clientService.getAllClients().spliterator(), false)
                .map(clientMapper::toResponseDto)
                .toList();

        model.addAttribute("clients",clientsToSelect);

        List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
        }

        model.addAttribute("cart", cart);
        model.addAttribute("orderRequestDto",new OrderRequestDto());
        return "/order/viewItemsOrder";
    }

    @GetMapping("/create/select")
    public String selectProduct(Model model){

        List<ProductResponseDto> productsToSelect = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                .map(productMapper::toResponseDto)
                .toList();

        model.addAttribute("allProducts",productsToSelect);

        return "/order/viewProductOrder";
    }


    @GetMapping("/create/select/addToCart/{code}")
    public String addToCart(@PathVariable("code") String code, Model model, HttpSession session) {

        ProductResponseDto product = productMapper.toResponseDto(productService.getProductByCode(code));
        List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
        }

        cart.add(product); // Добавляем товар в корзину
        session.setAttribute("cart", cart); // Сохраняем корзину в сессии
        //model.addAttribute("cart", cart); // Добавляем корзину в модель для отображения

        return "redirect:/orders/create"; // Вернуться на страницу с продуктами
    }


    @GetMapping("/create/del/{code}")
    public String delFromCart(@PathVariable("code") String code, HttpSession session) {

        List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

        if (cart != null) {
            cart.removeIf(product -> code.equals(product.getCode()));
            session.setAttribute("cart", cart);
        }

        return "redirect:/orders/create";
    }

    @PostMapping("/create")
    public String saveOrder(@Valid @ModelAttribute("orderRequestDto") OrderRequestDto orderRequestDto,
                            BindingResult errors, Model model, HttpSession session){

        // Проверка на ошибки валидации
        if(errors.hasErrors()){

            List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

            if (cart == null) {
                cart = new ArrayList<>();
            }

            model.addAttribute("cart", cart);
            List<ClientResponseDto> clientsToSelect = StreamSupport.stream(clientService.getAllClients().spliterator(), false)
                    .map(clientMapper::toResponseDto)
                    .toList();

            model.addAttribute("clients",clientsToSelect);

            return "/order/viewItemsOrder";
        }


        orderService.createOrder(orderRequestDto);

        session.removeAttribute("cart");

        return "redirect:/orders";
    }

    @GetMapping("/edit-order/{id}")
    public String editOrder(@PathVariable(value = "id") Long id, Model model){

        OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(id));
        model.addAttribute("order", order);
        model.addAttribute("orderRequestDto", new OrderRequestDto());

        return "/order/editOrder";
    }

    @GetMapping("/edit-order/{id}/del/{code}")
    public String delFromOrder(@PathVariable(value = "id") Long id, @PathVariable(value = "code") String code, Model model){

        orderItemService.removeItemFromOrder(id, code);

        return "redirect:/orders/edit-order/" + id;
    }


    @PostMapping("/edit-order")
    public String orderEdit(@ModelAttribute("order") @Valid OrderRequestDto orderRequestDto, BindingResult errors,
                            Model model, @RequestParam("orderId") Long orderId){

        // Проверка на ошибки валидации
        if(errors.hasErrors()){
            System.out.println("Ошибка" + " " + errors);
            OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(orderId));

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
