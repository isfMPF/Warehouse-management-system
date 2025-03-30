package com.project.wms.controller;

import com.project.wms.dto.requestdto.PromotionRequestDTO;
import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.dto.responsedto.PromotionResponseDTO;
import com.project.wms.mapper.PromotionMapper;
import com.project.wms.service.ProductService;
import com.project.wms.service.PromotionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class PromoController {

    @Autowired
    private ProductService productService;
    @Autowired
    private PromotionService promotionService;
    @Autowired
    private PromotionMapper promotionMapper;
    private static final Logger logger = LoggerFactory.getLogger(PromoController.class);

    @GetMapping("/promotions")
    public String show(Model model){
        try {
            List<PromotionResponseDTO> promotions = promotionService.getAllPromotions();
            model.addAttribute("promotions", promotions);
            return "promo/promo";
        } catch (Exception e) {
            logger.error("Ошибка при загрузки страницы", e);
            model.addAttribute("errorMessage", "Ошибка при загрузки страницы");
            return "error/error";
        }

    }

    @GetMapping("/promotions/create")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("promotion", new PromotionRequestDTO());
            model.addAttribute("allProducts", productService.getAllProducts());
            return "promo/addPromo";
        } catch (Exception e) {
            logger.error("Ошибка при создании промо", e);
            model.addAttribute("errorMessage", "Ошибка при создании промо");
            return "error/error";
        }

    }

    @PostMapping("/promotions/create")
    public String createPromotion(
            @ModelAttribute("promotion") @Valid PromotionRequestDTO promotionDTO,
            BindingResult result,
            Model model) {

        try {
            // Проверка на null (добавьте эту проверку первой)
            if (promotionDTO.getEndDate() == null || promotionDTO.getStartDate() == null) {
                result.rejectValue("endDate", "error.date.null", "Даты начала и окончания обязательны");
            }
            // Затем остальная валидация
            else if (promotionDTO.getEndDate().isBefore(promotionDTO.getStartDate())) {
                result.rejectValue("endDate", "error.endDate",
                        "Дата окончания должна быть не раньше даты начала");
            }

            if (result.hasErrors()) {
                model.addAttribute("allProducts", productService.getAllProducts());
                return "promo/addPromo";
            }

            promotionService.createPromo(promotionDTO);
            return "redirect:/promotions";
        } catch (Exception e) {
            logger.error("Ошибка при создании промо", e);
            model.addAttribute("errorMessage", "Ошибка при создании промо");
            return "error/error";
        }

    }



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/promotions/delete/{id}")
    public String deletePromotion(@PathVariable("id") Long id, Model model) {
        try {
            promotionService.deletePromotion(id);
            return "redirect:/promotions";
        } catch (Exception e) {
            logger.error("Ошибка при удалении промо", e);
            model.addAttribute("errorMessage", "Ошибка при удалении промо");
            return "error/error";
        }
    }


}
