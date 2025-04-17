package com.project.wms.controller;

import com.project.wms.dto.requestdto.PromotionRequestDTO;
import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.dto.responsedto.PromotionResponseDTO;
import com.project.wms.mapper.ProductMapper;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/promotions")
public class PromoController {

    private final ProductService productService;
    private final PromotionService promotionService;
    private final PromotionMapper promotionMapper;
    private final ProductMapper productMapper;
    private static final Logger logger = LoggerFactory.getLogger(PromoController.class);

    @Autowired
    public PromoController(ProductService productService,
                           PromotionService promotionService,
                           PromotionMapper promotionMapper, ProductMapper productMapper) {
        this.productService = productService;
        this.promotionService = promotionService;
        this.promotionMapper = promotionMapper;
        this.productMapper = productMapper;
    }

    @GetMapping
    public String showAllPromotions(Model model) {
        try {
            List<PromotionResponseDTO> promotions = promotionService.getAllPromotions();

            // Логика вычисления displayRequiredQuantity для каждой промоакции
            for (PromotionResponseDTO promo : promotions) {
                // Проверяем, есть ли в includedProducts товар с таким же кодом, как у requiredProduct
                boolean hasRequiredProduct = promo.getIncludedProducts().stream()
                        .anyMatch(p -> p.getProductResponseDto().getCode().equals(promo.getRequiredProduct().getCode()));

                // Если есть, уменьшаем на 1, иначе оставляем как есть
                promo.setDisplayRequiredQuantity(hasRequiredProduct ? promo.getRequiredQuantity() - 1 : promo.getRequiredQuantity());
            }

            model.addAttribute("promotions", promotions);
            return "promo/promo";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке страницы промоакций", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке списка акций");
            return "error/error";
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        try {
            PromotionRequestDTO promotionDTO = new PromotionRequestDTO();
            List<ProductResponseDto> productResponseDtos = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                    .map(productMapper::toResponseDto)
                    .toList();
            model.addAttribute("promotionRequestDTO", promotionDTO);
            model.addAttribute("allProducts", productResponseDtos);
            return "promo/addPromo";
        } catch (Exception e) {
            logger.error("Ошибка при открытии формы создания акции", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке формы");
            return "error/error";
        }
    }

    @PostMapping("/create")
    public String createPromotion(
            @ModelAttribute("promotionRequestDTO") @Valid PromotionRequestDTO promotionDTO,
            BindingResult result,
            Model model) {

        try {
            // Дополнительная валидация дат
            if (promotionDTO.getStartDate() != null && promotionDTO.getEndDate() != null
                    && promotionDTO.getEndDate().isBefore(promotionDTO.getStartDate())) {
                result.rejectValue("endDate", "date.invalid",
                        "Дата окончания должна быть после даты начала");
            }

//            // Проверка выбранных товаров
//            if (promotionDTO.getFreeProduct() != null
//                    && promotionDTO.getFreeProduct().getCode() != null
//                    && promotionDTO.getRequiredProduct() != null
//                    && promotionDTO.getRequiredProduct().getCode() != null
//                    && promotionDTO.getFreeProduct().getCode()
//                    .equals(promotionDTO.getRequiredProduct().getCode())) {
//                result.rejectValue("freeProduct.code", "product.duplicate",
//                        "Бесплатный и обязательный товар не могут совпадать");
//            }

            if (result.hasErrors()) {
                List<ProductResponseDto> productResponseDtos = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                        .map(productMapper::toResponseDto)
                        .toList();
                model.addAttribute("allProducts", productResponseDtos);
                return "promo/addPromo";
            }

            promotionService.createPromo(promotionDTO);

            return "redirect:/promotions";
        } catch (Exception e) {
            logger.error("Ошибка при создании акции", e);
            model.addAttribute("errorMessage", "Ошибка при создании акции: " + e.getMessage());
            model.addAttribute("allProducts", productService.getAllProducts());
            return "promo/addPromo";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deletePromotion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            promotionService.deletePromotion(id);
            redirectAttributes.addFlashAttribute("successMessage", "Акция успешно удалена");
        } catch (Exception e) {
            logger.error("Ошибка при удалении акции ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при удалении акции: " + e.getMessage());
        }
        return "redirect:/promotions";
    }
}