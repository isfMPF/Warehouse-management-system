package com.project.wms.controller;

import com.project.wms.dto.requestdto.TransportRequestDto;
import com.project.wms.service.Transportservice;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transport")
public class TransportController {

    @Autowired
    private Transportservice transportservice;
    private static final Logger logger = LoggerFactory.getLogger(TransportController.class);


    @GetMapping
    public String showTransport(Model model) {
        try {
            model.addAttribute("transports", transportservice.findAll());
            return "transport/transport";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке страницы", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке страницы");
            return "error/error";
        }

    }

    @GetMapping("/add-transport")
    public String transport(Model model) {
        try {
            model.addAttribute("transportRequestDto", new TransportRequestDto());
            return "transport/addTransport";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке страницы", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке страницы");
            return "error/error";
        }

    }

    @PostMapping("/save-transport")
    public String addTransport(@ModelAttribute("transportRequestDto") @Valid TransportRequestDto transportRequestDto,
                               BindingResult errors, Model model) {
        try {

            if (errors.hasErrors()) {
                return "transport/addTransport";
            }


            transportservice.save(transportRequestDto);
            return "redirect:/transport";
        } catch (Exception e) {
            logger.error("Ошибка при добавлении транспорта", e);
            model.addAttribute("errorMessage", "Ошибка при добавлении транспорта");
            return "error/error";
        }
    }

    @GetMapping("/transportEdit/{id}")
    public String edit(@PathVariable(value = "id") Long id, Model model) {
        try {
            model.addAttribute("transportRequestDto", transportservice.findById(id));
            return "transport/addTransport";
        } catch (Exception e) {
            logger.error("Ошибка при редактировании транспорта", e);
            model.addAttribute("errorMessage", "Ошибка при редактировании транспорта");
            return "error/error";
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/transportDelete/{id}")
    public String deleteTransport(@PathVariable Long id, Model model) {

        try {
            transportservice.delete(id);
            return "redirect:/transport";
        } catch (Exception e) {
            logger.error("Ошибка при удалении транспорта", e);
            model.addAttribute("errorMessage", "Ошибка при удалении транспорта");
            return "error/error";
        }
    }

}
