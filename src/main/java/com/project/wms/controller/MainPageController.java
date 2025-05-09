package com.project.wms.controller;

import com.project.wms.dto.responsedto.OrderResponseDto;
import com.project.wms.mapper.OrderMapper;
import com.project.wms.service.OrderService;
import com.project.wms.util.OrderMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Controller
public class MainPageController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    private static final Logger logger = LoggerFactory.getLogger(MainPageController.class);

    @GetMapping("/main")
    public String showInfo(Model model){

        try {
            List<OrderResponseDto> orders = orderService.getOrdersBetweenDates(LocalDate.now(),LocalDate.now()).stream()
                    .map(orderMapper::toResponseDto)
                    .toList();

            OrderMetrics metrics = calculateMetrics(orders);

            model.addAttribute("balance", metrics.getTotalSum());
            model.addAttribute("customers", metrics.getUniqueClientCount());
            model.addAttribute("newOrders", metrics.getOrderCount());
            model.addAttribute("averageCheck", metrics.getAverageCheck());


            return "main/mainPage";
        } catch (Exception e) {
            logger.error("Ошибка при формировании статистики", e);
            model.addAttribute("errorMessage", "Ошибка при формировании статистики");
            return "error/error";
        }

    }

    @GetMapping("main/search")
    public String search(
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate start, @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate end, Model model)
    {

        try {
            List<OrderResponseDto> orders = orderService.getOrdersBetweenDates(start,end).stream()
                    .map(orderMapper::toResponseDto)
                    .toList();

            // Рассчитываем метрики
            OrderMetrics metrics = calculateMetrics(orders);

            model.addAttribute("balance", metrics.getTotalSum());
            model.addAttribute("customers", metrics.getUniqueClientCount());
            model.addAttribute("newOrders", metrics.getOrderCount());
            model.addAttribute("averageCheck", metrics.getAverageCheck());

            return "main/mainPage";
        } catch (Exception e) {
            logger.error("Ошибка при поиске", e);
            model.addAttribute("errorMessage", "Ошибка при поиске");
            return "error/error";
        }

    }

    private OrderMetrics calculateMetrics(List<OrderResponseDto> orders) {

        double totalSum = orders.stream()
                .mapToDouble(OrderResponseDto::getTotal)
                .sum();

        long uniqueClientCount = orders.stream()
                .map(OrderResponseDto::getCodeClient)
                .count();

        long orderCount = orders.size();

        int averageCheck = orderCount == 0 ? 0 : (int) (totalSum / orderCount);

        return new OrderMetrics(totalSum, uniqueClientCount, orderCount, averageCheck);
    }
}
