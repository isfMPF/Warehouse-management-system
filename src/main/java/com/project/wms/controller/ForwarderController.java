package com.project.wms.controller;

import com.project.wms.dto.requestdto.ForwarderRequestDto;
import com.project.wms.dto.requestdto.TransportRequestDto;
import com.project.wms.service.ForwarderService;
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
@RequestMapping("/forwarder")
public class ForwarderController {

    @Autowired
    private ForwarderService forwarderService;
    private static final Logger logger = LoggerFactory.getLogger(ForwarderController.class);

    @GetMapping
    public String showForwarders(Model model) {
        try {
            model.addAttribute("forwarders", forwarderService.findAll());
            return "forwarder/forwarder";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке страницы", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке страницы");
            return "error/error";
        }
    }

    @GetMapping("/add-forwarder")
    public String forwarder(Model model) {
        try {
            model.addAttribute("forwarderRequestDto", new ForwarderRequestDto());
            return "forwarder/addForwarder";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке страницы", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке страницы");
            return "error/error";
        }
    }

    @PostMapping("/save-forwarder")
    public String addForwarder(@ModelAttribute("transportRequestDto") @Valid ForwarderRequestDto forwarderRequestDto,
                               BindingResult errors, Model model) {
        try {

            if (errors.hasErrors()) {
                return "forwarder/addForwarder";
            }


            forwarderService.save(forwarderRequestDto);
            return "redirect:/forwarder";
        } catch (Exception e) {
            logger.error("Ошибка при добавлении экспедитора", e);
            model.addAttribute("errorMessage", "Ошибка при добавлении экспедитора");
            return "error/error";
        }
    }

    @GetMapping("/forwarderEdit/{id}")
    public String edit(@PathVariable(value = "id") Long id, Model model) {
        try {
            model.addAttribute("forwarderRequestDto", forwarderService.findById(id));
            return "forwarder/addForwarder";
        } catch (Exception e) {
            logger.error("Ошибка при редактировании экспедитора", e);
            model.addAttribute("errorMessage", "Ошибка при редактировании экспедитора");
            return "error/error";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/forwarderDelete/{id}")
    public String deleteTransport(@PathVariable Long id, Model model) {

        try {
            forwarderService.delete(id);
            return "redirect:/forwarder";
        } catch (Exception e) {
            logger.error("Ошибка при удалении экспедитора", e);
            model.addAttribute("errorMessage", "Ошибка при удалении экспедитора");
            return "error/error";
        }
    }

}
