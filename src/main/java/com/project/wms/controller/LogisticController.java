package com.project.wms.controller;

import com.project.wms.dto.responsedto.OrderResponseDto;
import com.project.wms.mapper.OrderMapper;
import com.project.wms.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class LogisticController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/logistics")
    public String showOrders(Model model){

        List<OrderResponseDto> orders = orderService.getOrdersBetweenDates(LocalDate.now().minusDays(2), LocalDate.now().minusDays(2))
                .stream()
                .map(orderMapper::toResponseDto)
                .toList();

        System.out.println(LocalDate.now().minusDays(1));
        System.out.println(orders);
        model.addAttribute("orders", orders);


        return "logistic/logistic";
    }
}
