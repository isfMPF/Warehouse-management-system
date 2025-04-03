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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @GetMapping
    public String showAllOrders(Model model) {

        try {
            List<OrderResponseDto> orders = orderService.getOrdersBetweenDates(LocalDate.now(),LocalDate.now()).stream()
                    .map(orderMapper::toResponseDto)  // Используем OrderMapper для преобразования заказа в DTO
                    .collect(Collectors.toList());
            model.addAttribute("orders", orders);

            return "order/viewOrders";
        } catch (Exception e) {
            logger.error("Ошибка при загрузки страницы", e);
            model.addAttribute("errorMessage", "Ошибка при загрузки страницы");
            return "error/error";
        }

    }



    @GetMapping("/search")
    public String searchBetween(
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate start, @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate end, Model model)
    {
        try {
            List<OrderResponseDto> orders = orderService.getOrdersBetweenDates(start, end).stream()
                    .map(orderMapper::toResponseDto)
                    .toList();

            model.addAttribute("orders", orders);

            return "order/viewOrders";
        } catch (Exception e) {
            logger.error("Ошибка при поиске", e);
            model.addAttribute("errorMessage", "Ошибка при поиске");
            return "error/error";
        }


    }

    @GetMapping("/orderDetails/{id}")
    public String showDetailsOrder(@PathVariable(value = "id") long id, Model model) {

        try {
            OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(id));
            model.addAttribute("orders", order);
            return "order/detailsOrder";
        } catch (Exception e) {
            logger.error("Ошибка при формировании заказа", e);
            model.addAttribute("errorMessage", "Ошибка при формировании заказа");
            return "error/error";
        }

    }

    @GetMapping("/create")
    public String createOrder(Model model, HttpSession session){

        try {
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
            return "order/viewItemsOrder";
        } catch (Exception e) {
            logger.error("Ошибка при создании заказа", e);
            model.addAttribute("errorMessage", "Ошибка при создании заказа");
            return "error/error";
        }


    }

    @GetMapping("/create/select")
    public String selectProduct(Model model){

        try {
            List<ProductResponseDto> productsToSelect = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                    .filter(product -> product.getAmount() > 5)
                    .map(productMapper::toResponseDto)
                    .toList();

            model.addAttribute("allProducts",productsToSelect);

            return "order/viewProductOrder";
        } catch (Exception e) {
            logger.error("Ошибка при выборе товара", e);
            model.addAttribute("errorMessage", "Ошибка при выборе товара");
            return "error/error";
        }

    }


    @GetMapping("/create/select/addToCart/{code}")
    public String addToCart(@PathVariable("code") String code, Model model, HttpSession session) {

        try {
            ProductResponseDto product = productMapper.toResponseDto(productService.getProductByCode(code));
            List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

            if (cart == null) {
                cart = new ArrayList<>();
            }

            // Проверяем, существует ли товар в корзине
            boolean productExists = false;
            for (ProductResponseDto item : cart) {
                if (item.getCode().equals(product.getCode())) {
                    productExists = true;
                    break;
                }
            }

            // Если товара нет в корзине, добавляем его
            if (!productExists) {
                cart.add(product);
            }

            session.setAttribute("cart", cart); // Сохраняем корзину в сессии

            return "redirect:/orders/create"; // Вернуться на страницу с продуктами
        } catch (Exception e) {
            logger.error("Ошибка при выборе товара", e);
            model.addAttribute("errorMessage", "Ошибка при выборе товара");
            return "error/error";
        }

    }


    @GetMapping("/create/del/{code}")
    public String delFromCart(@PathVariable("code") String code, HttpSession session, Model model) {

        try {
            List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

            if (cart != null) {
                cart.removeIf(product -> code.equals(product.getCode()));
                session.setAttribute("cart", cart);
            }

            return "redirect:/orders/create";
        } catch (Exception e) {
            logger.error("Ошибка при удалении товара из корзины", e);
            model.addAttribute("errorMessage", "Ошибка при удалении товара из корзины");
            return "error/error";
        }

    }

    @PostMapping("/create")
    public String saveOrder(@Valid @ModelAttribute("orderRequestDto") OrderRequestDto orderRequestDto,
                            BindingResult errors, Model model, HttpSession session){

        try {
            // Проверка на ошибки валидации
            if(errors.hasErrors()){
                System.out.println(errors);
                List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

                if (cart == null) {
                    cart = new ArrayList<>();
                }

                model.addAttribute("cart", cart);
                List<ClientResponseDto> clientsToSelect = StreamSupport.stream(clientService.getAllClients().spliterator(), false)
                        .map(clientMapper::toResponseDto)
                        .toList();

                model.addAttribute("clients",clientsToSelect);

                return "order/viewItemsOrder";
            }


            orderService.createOrder(orderRequestDto);

            session.removeAttribute("cart");

            return "redirect:/orders";
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            return "error/error";
        }catch (Exception e) {
            logger.error("Ошибка при создании заказа", e);
            model.addAttribute("errorMessage", "Ошибка при создании заказа");
            return "error/error";
        }


    }

    @GetMapping("/edit-order/{id}")
    public String editOrder(@PathVariable(value = "id") Long id, Model model, HttpSession session){

        try {
            List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

            OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(id));

            if (cart == null) {

                List <ProductResponseDto> productResponseDto = productMapper.toProductResponseDto(order.getItem());

                session.setAttribute("cart", productResponseDto);

                model.addAttribute("client", order);

                model.addAttribute("cart", productResponseDto);
            }else {
                model.addAttribute("client", order);
                model.addAttribute("cart", cart);
            }

            model.addAttribute("orderRequestDto", new OrderRequestDto());

            return "order/editOrder";
        } catch (Exception e) {
            logger.error("Ошибка при редактировании заказа", e);
            model.addAttribute("errorMessage", "Ошибка при редактировании заказа");
            return "error/error";
        }

    }

    @GetMapping("/edit-order/{id}/del/{code}")
    public String delFromOrder(@PathVariable(value = "id") Long id, @PathVariable(value = "code") String code, Model model, HttpSession session) {

        try {
            List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

            if (cart != null) {
                cart.removeIf(product -> code.equals(product.getCode()));
                session.setAttribute("cart", cart);
            }

            return "redirect:/orders/edit-order/" + id;
        }catch (Exception e) {
            logger.error("Ошибка при удалении товара из корзины", e);
            model.addAttribute("errorMessage", "Ошибка при удалении товара из корзины");
            return "error/error";
        }


    }

    @GetMapping("/edit-order/{id}/select")
    public String selectProducts(@PathVariable(value = "id") Long id, Model model){

        try {
            List<ProductResponseDto> productsToSelect = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                    .filter(product -> product.getAmount() > 5)
                    .map(productMapper::toResponseDto)
                    .toList();

            OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(id));

            model.addAttribute("allProducts",productsToSelect);

            model.addAttribute("order", order);
            return "order/viewProductOrderWhenEditing";
        } catch (Exception e) {
            logger.error("Ошибка при выборе товара (вовремя редактировании заказа)", e);
            model.addAttribute("errorMessage", "Ошибка при выборе товара");
            return "error/error";
        }

    }


    @GetMapping("/order-edit/{id}/select/addToCart/{code}")
    public String addToCartWhenEditing(@PathVariable(value = "id") Long id,
                                       @PathVariable("code") String code,
                                       Model model,
                                       HttpSession session) {

        try {
            // Получаем товар по коду
            ProductResponseDto product = productMapper.toResponseDto(productService.getProductByCode(code));

            // Получаем корзину из сессии
            List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

            // Если корзина не существует, создаем новую
            if (cart == null) {
                cart = new ArrayList<>();
            }

            // Проверяем, существует ли товар в корзине
            boolean productExists = false;
            for (ProductResponseDto item : cart) {
                if (item.getCode().equals(product.getCode())) {
                    // Если товар уже есть в корзине, увеличиваем его количество на 1
                    item.setAmount(item.getAmount() + 1);
                    productExists = true;
                    break;
                }
            }

            // Если товара нет в корзине, добавляем его с количеством 1
            if (!productExists) {
                product.setAmount(1); // Устанавливаем начальное количество
                cart.add(product);
            }

            // Сохраняем обновленную корзину в сессии
            session.setAttribute("cart", cart);

            // Перенаправляем на страницу редактирования заказа
            return "redirect:/orders/edit-order/" + id;
        } catch (Exception e) {
            logger.error("Ошибка при выборе товара", e);
            model.addAttribute("errorMessage", "Ошибка при выборе товара");
            return "error/error";
        }

    }



    @PostMapping("/edit-order")
    public String orderEdit(@Valid @ModelAttribute("orderRequestDto") OrderRequestDto orderRequestDto, BindingResult errors,
                            Model model, @RequestParam("orderId") Long orderId, HttpSession session){

        try {
            orderService.deleteOrderWithItems(orderId);
            // Проверка на ошибки валидации
            if(errors.hasErrors()){
                System.out.println("Ошибка" + " " + errors);
                OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(orderId));

                List<ProductResponseDto> productsToSelect = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                        .map(productMapper::toResponseDto)
                        .toList();

                System.out.println(order);

                model.addAttribute("products",productsToSelect);
                model.addAttribute("order", order);

                return "order/editOrder";
            }


            orderService.createOrder(orderRequestDto);

            session.removeAttribute("cart");

            return "redirect:/orders";
        }catch (Exception e) {
            logger.error("Ошибка при редактировании заказа", e);
            model.addAttribute("errorMessage", "Ошибка при редактировании заказа");
            return "error/error";
        }

    }

    @GetMapping("/edit-order/{id}/cancel")
    public String cancel(@PathVariable(value = "id") Long id, HttpSession session, Model model){

        try {
            List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

            if (cart != null) {
                session.removeAttribute("cart");
            }

            return "redirect:/orders";
        }catch (Exception e) {
            logger.error("Ошибка при редактировании заказа (cancel)", e);
            model.addAttribute("errorMessage", "Ошибка при редактировании заказа");
            return "error/error";
        }

    }

    @GetMapping("/cancel")
    public String cancelOrder(HttpSession session, Model model){

        try{
            List<ProductResponseDto> cart = (List<ProductResponseDto>) session.getAttribute("cart");

            if (cart != null) {
                session.removeAttribute("cart");
            }

            return "redirect:/orders";
        }catch (Exception e) {
            logger.error("Ошибка при отмене", e);
            model.addAttribute("errorMessage", "Ошибка при отмене");
            return "error/error";
        }


    }

    @GetMapping("/promo/{id}")
    public String promo(@PathVariable(value = "id") Long id, Model model) {
        try {
            Map<String, Object> promoResult = orderService.calculatePromo(id);
            OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(id));

            model.addAttribute("orders", order);
            model.addAttribute("promoMessages", promoResult.get("messages"));
            model.addAttribute("promoApplied", promoResult.get("promoApplied"));

            return "order/viewOrders";
        } catch (Exception e) {
            logger.error("Ошибка при формировании промо", e);
            model.addAttribute("errorMessage", "Ошибка при формировании промо: " + e.getMessage());
            return "error/error";
        }
    }

    @PostMapping("/return/{id}")
    public String returnOrder(@PathVariable(value = "id") Long id, Model model){

        try {
            orderService.returnOrder(id);
            return "redirect:/orders";

        } catch (Exception e) {
            logger.error("Ошибка при возврате заказа", e);
            model.addAttribute("errorMessage", "Ошибка при возврате заказа");
            return "error/error";
        }


    }

}
