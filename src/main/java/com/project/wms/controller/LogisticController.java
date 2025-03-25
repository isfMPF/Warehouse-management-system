package com.project.wms.controller;

import com.project.wms.dto.responsedto.OrderItemResponseDto;
import com.project.wms.dto.responsedto.OrderResponseDto;
import com.project.wms.entity.OrderItemEntity;
import com.project.wms.mapper.OrderItemMapper;
import com.project.wms.mapper.OrderMapper;
import com.project.wms.service.OrderItemService;
import com.project.wms.service.OrderService;
import com.project.wms.util.ExcelExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LogisticController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/logistics")
    public String showOrders(Model model){

        List<OrderResponseDto> orders = orderService.getOrdersBetweenDates(LocalDate.now().minusDays(1), LocalDate.now().minusDays(1))
                .stream()
                .map(orderMapper::toResponseDto)
                .toList();


        model.addAttribute("orders", orders);

        return "logistic/logistic";
    }

    @GetMapping("/logistics/search")
    public String search( @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                              LocalDate start, @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                              LocalDate end, Model model){

        List<OrderResponseDto> orders = orderService.getOrdersBetweenDates(start,end).stream()
                .map(orderMapper::toResponseDto)
                .toList();

        model.addAttribute("orders", orders);

        return "logistic/logistic";

    }

    @PostMapping("/logistics/process-selected")
    public String processSelectedOrders(@RequestParam(name = "selectedOrderIds", required = false) List<Long> selectedOrderIds,
                                         Model model){

        List<OrderResponseDto> orders = new ArrayList<>();

        if (selectedOrderIds == null || selectedOrderIds.isEmpty()) {
            model.addAttribute("errorMessage", "Пожалуйста, выберите хотя бы один заказ");
            return "logistic/logistic";
        }

        if (selectedOrderIds != null) {
            System.out.println("Выбранные ID: " + selectedOrderIds);

            for (Long selectedOrderId : selectedOrderIds) {
                OrderResponseDto orderResponseDto = orderMapper.toResponseDto(orderService.getOrderById(selectedOrderId));
                orders.add(orderResponseDto);
            }
            model.addAttribute("orders",orders);
        }

        return "logistic/information";
    }


    @PostMapping("/logistics/export")
    public String exportOrder(
            @RequestParam(name = "selectedOrderIds", required = false) List<Long> selectedOrderIds,
            HttpServletResponse response) throws IOException {

        if (selectedOrderIds == null || selectedOrderIds.isEmpty()) {
            return null;
        }

        List<OrderResponseDto> orders = new ArrayList<>();
        String exportDate = "";

        for (Long selectedOrderId : selectedOrderIds) {
            OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(selectedOrderId));
            orders.add(order);


            if (exportDate.isEmpty() && order.getDate() != null) {
                exportDate = order.getDate().toString().replace("-", "");
            }
        }

        // Формируем имя файла
        String filename = "заказы(от)_" + (exportDate.isEmpty() ? "" : exportDate) + ".xlsx";

        byte[] excelBytes = ExcelExporter.exportOrdersToExcel(orders);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.getOutputStream().write(excelBytes);
        response.flushBuffer();

        return null;
    }




}
