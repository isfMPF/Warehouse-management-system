package com.project.wms.controller;

import com.project.wms.dto.responsedto.ForwarderResponseDto;
import com.project.wms.dto.responsedto.OrderResponseDto;
import com.project.wms.dto.responsedto.OrderResponseDtoToExport;
import com.project.wms.dto.responsedto.TransportResponseDto;
import com.project.wms.mapper.*;
import com.project.wms.service.ForwarderService;
import com.project.wms.service.OrderService;
import com.project.wms.service.Transportservice;
import com.project.wms.util.ExcelExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class LogisticController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private Transportservice transportservice;
    @Autowired
    private ForwarderService forwarderService;
    @Autowired
    private OrderMapperToExport orderMapperToExport;

    private static final Logger logger = LoggerFactory.getLogger(LogisticController.class);

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/logistics")
    public String showOrders(Model model){

        try {
            List<OrderResponseDto> orders = orderService.getOrdersBetweenDates(LocalDate.now(), LocalDate.now())
                    .stream()
                    .map(orderMapper::toResponseDto)
                    .toList();


            model.addAttribute("orders", orders);
            model.addAttribute("transports", transportservice.findAll());
            model.addAttribute("forwarders", forwarderService.findAll());
            return "logistic/logistic";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке заказов", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке заказов");
            return "error/error";
        }

    }

    @GetMapping("/logistics/search")
    public String search( @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                              LocalDate start, @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                              LocalDate end, Model model){

        try {
            List<OrderResponseDto> orders = orderService.getOrdersBetweenDates(start,end).stream()
                    .map(orderMapper::toResponseDto)
                    .toList();

            model.addAttribute("orders", orders);
            model.addAttribute("transports", transportservice.findAll());
            model.addAttribute("forwarders", forwarderService.findAll());
            return "logistic/logistic";
        } catch (Exception e) {
            logger.error("Ошибка при поиске заказов", e);
            model.addAttribute("errorMessage", "Ошибка при поиске заказов");
            return "error/error";
        }


    }

    @PostMapping("/logistics/process-selected")
    public String processSelectedOrders(
            @RequestParam(name = "selectedOrderIds", required = false) List<Long> selectedOrderIds,
            @RequestParam(name = "transportId") Long transportId,
            @RequestParam(name = "checkCapacity",  required = false) boolean capacityCheck,
            @RequestParam(name = "forwarderId") Long forwarderId, Model model) {

        try{
            if (selectedOrderIds == null || selectedOrderIds.isEmpty()) {
                model.addAttribute("errorMessage", "Пожалуйста, выберите хотя бы один заказ");
                return "logistic/logistic";
            }

            double totalWeight = 0.0;
            List<OrderResponseDto> orders = new ArrayList<>();
            for (Long selectedOrderId : selectedOrderIds) {
                OrderResponseDto order = orderMapper.toResponseDto(orderService.getOrderById(selectedOrderId));
                orders.add(order);
                totalWeight += orderService.calculateWeightOrderById(selectedOrderId);
            }

            double transportCapacity = transportservice.findById(transportId)
                    .map(TransportResponseDto::getCapacity)
                    .orElse(0.0);

            if (capacityCheck && totalWeight > transportCapacity){
                model.addAttribute("errorMessage", "Вес заказов превышает грузоподъёмность транспорта");
                return "error/error";
            }

            // Сортируем товары внутри каждого заказа по объему (от большего к меньшему)
            for (OrderResponseDto order : orders) {
                if (order.getItem() != null) {
                    order.getItem().sort((item1, item2) -> {
                        double volume1 = parseVolume(item1.getVolume());
                        double volume2 = parseVolume(item2.getVolume());
                        return Double.compare(volume2, volume1); // Сортировка по убыванию
                    });
                }
            }

            model.addAttribute("orders", orders);
            model.addAttribute("transport", transportId);
            model.addAttribute("forwarder", forwarderId);
            return "logistic/information";
        } catch (Exception e) {
            logger.error("Ошибка при формировании заказов", e);
            model.addAttribute("errorMessage", "Ошибка при формировании заказов");
            return "error/error";
        }

    }

    // Вспомогательный метод для парсинга объема (удаляет "л" и преобразует в число)
    private double parseVolume(String volume) {
        try {
            return Double.parseDouble(volume.replace(" л", "").replace(",", ".").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }


    @PostMapping("/logistics/export")
    public String exportOrder(@RequestParam(name = "selectedOrderIds", required = false) List<Long> selectedOrderIds,
            HttpServletResponse response, @RequestParam(name = "transportId") Long transportId,
                              @RequestParam(name = "forwarderId") Long forwarderId, Model model){
        try {
            if (selectedOrderIds == null || selectedOrderIds.isEmpty()) {
                return null;
            }

            String transport = transportservice.findById(transportId)
                    .map(TransportResponseDto::getInfo)
                    .orElse("Без транспорта");

            String forwarder = forwarderService.findById(forwarderId)
                    .map(ForwarderResponseDto::getFullName)
                    .orElse("Без экспедитора");




            List<OrderResponseDtoToExport> orders = new ArrayList<>();
            String exportDate = "";

            // Получаем и обрабатываем заказы
            for (Long selectedOrderId : selectedOrderIds) {
                OrderResponseDtoToExport order = orderMapperToExport.toResponseDto(orderService.getOrderById(selectedOrderId));

                order.setForwarder(forwarder);
                order.setTransport(transport);

                orders.add(order);


                // Получаем дату для имени файла (из первого заказа с датой)
                if (exportDate.isEmpty() && order.getDate() != null) {
                    exportDate = order.getDate().toString().replace("-", "");
                }
            }

            // Сортируем товары внутри каждого заказа по объему (от большего к меньшему)
            for (OrderResponseDtoToExport order : orders) {
                if (order.getItem() != null) {
                    order.getItem().sort((item1, item2) -> {
                        double volume1 = parseVolumeExport(item1.getVolume());
                        double volume2 = parseVolumeExport(item2.getVolume());
                        return Double.compare(volume2, volume1); // Сортировка по убыванию
                    });
                }
            }

            // Формируем имя файла
            String filename = "orders_" + (exportDate.isEmpty() ? "" : exportDate) + ".xlsx";

            // Экспортируем в Excel (уже с отсортированными товарами)
            byte[] excelBytes = ExcelExporter.exportOrdersToExcel(orders);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.getOutputStream().write(excelBytes);
            response.flushBuffer();
            return null;

        } catch (Exception e) {
            logger.error("Ошибка при экспорте", e);
            model.addAttribute("errorMessage", "Ошибка при экспорте");
            return "error/error";
        }
    }

    // Улучшенный метод парсинга объема

   private double parseVolumeExport(String volume) {
        if (volume == null || volume.isEmpty()) return 0.0;
        try {
            // Заменяем запятые на точки и удаляем все нецифровые символы кроме точки
            String normalized = volume.replace(",", ".")
                    .replaceAll("[^0-9.]", "");
            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }




}
