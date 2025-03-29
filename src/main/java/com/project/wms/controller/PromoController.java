package com.project.wms.controller;

import com.project.wms.dto.requestdto.PromotionRequestDTO;
import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.dto.responsedto.PromotionResponseDTO;
import com.project.wms.mapper.PromotionMapper;
import com.project.wms.service.ProductService;
import com.project.wms.service.PromotionService;
import jakarta.validation.Valid;
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

    @GetMapping("/promotions")
    public String show(Model model){
        List<PromotionResponseDTO> promotions = promotionService.getAllPromotions();
        model.addAttribute("promotions", promotions);
        return "promo/promo";
    }

    @GetMapping("/promotions/create")
    public String showCreateForm(Model model) {
        model.addAttribute("promotion", new PromotionRequestDTO());
        model.addAttribute("allProducts", productService.getAllProducts());
        return "promo/addPromo";
    }

    @PostMapping("/promotions/create")
    public String createPromotion(
            @ModelAttribute("promotion") @Valid PromotionRequestDTO promotionDTO,
            BindingResult result,
            Model model) {

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
    }



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/promotions/delete/{id}")
    public String deletePromotion(@PathVariable("id") Long id) {
        promotionService.deletePromotion(id);
        return "redirect:/promotions";
    }


}
