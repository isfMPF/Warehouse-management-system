package com.project.wms.controller;

import com.project.wms.dto.requestdto.ProductRequestDto;
import com.project.wms.dto.responsedto.ClientResponseDto;
import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.mapper.ProductMapper;
import com.project.wms.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class ProductController {
    public ProductService productService;
    public ProductMapper productMapper;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping("/products")
    public String showAllProducts(Model model){

        try {
            List<ProductResponseDto> productResponseDtos = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                    .map(productMapper::toResponseDto)
                    .toList();

            model.addAttribute("allProducts", productResponseDtos);
            return "/product/viewProduct";
        }catch (Exception e) {
            logger.error("Ошибка при загрузки страницы", e);
            model.addAttribute("errorMessage", "Ошибка при загрузки страницы");
            return "error/error";
        }

    }

    @GetMapping("/form-product")
    public String showFormsAddProduct(Model model){

        try {
            model.addAttribute("productRequestDto", new ProductRequestDto());
            return "/product/addProduct";
        } catch (Exception e) {
            logger.error("Ошибка при загрузки страницы", e);
            model.addAttribute("errorMessage", "Ошибка при загрузки страницы");
            return "error/error";
        }


    }

    @PostMapping("/add-product")
    public String addClient(@ModelAttribute("productRequestDto") @Valid ProductRequestDto productRequestDto,
                            BindingResult errors, Model model){

        try {
            if(errors.hasErrors())
            {
                return "/product/addProduct";
            }

            productService.addProduct(productRequestDto);
            return "redirect:/products";
        } catch (Exception e) {
            logger.error("Ошибка при добавлении товара", e);
            model.addAttribute("errorMessage", "Ошибка при добавлении товара");
            return "error/error";
        }


    }

    @GetMapping("/productEdit/{id}")
    public String Edit(@PathVariable(value = "id") long id, Model model) {

        try {
            ProductResponseDto productResponseDto = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                    .map(productMapper::toResponseDto)
                    .filter(product -> product.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Товар не найден"));

            model.addAttribute("product", productResponseDto);
            model.addAttribute("productRequestDto", new ProductRequestDto());
            return "/product/editProduct";
        } catch (Exception e) {
            logger.error("Ошибка при редактировании товара", e);
            model.addAttribute("errorMessage", "Ошибка при редактировании товара");
            return "error/error";
        }


    }

    @PostMapping("/edit-product")
    public String editClient(@ModelAttribute("product") @Valid ProductRequestDto productRequestDto,
                            BindingResult errors, Model model){

        try {
            if(errors.hasErrors())
            {
                return "/product/editProduct";
            }

            productService.addProduct(productRequestDto);
            return "redirect:/products";
        } catch (Exception e) {
            logger.error("Ошибка при редактировании товара", e);
            model.addAttribute("errorMessage", "Ошибка при редактировании товара");
            return "error/error";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/productDelete/{id}")
    public String deleteClient(@PathVariable(value = "id") long id, Model model){
        try {
            productService.deteleProduct(id);
            return "redirect:/products";
        } catch (Exception e) {
            logger.error("Ошибка при удалении товара", e);
            model.addAttribute("errorMessage", "Ошибка при удалении товара");
            return "error/error";
        }


    }

    @GetMapping("/search-product")
    public String showProductByNameOrByCode(@RequestParam String query, Model model){

        try {
            if (query == null || query.trim().isEmpty()) {
                model.addAttribute("error", "Введите название для поиска.");
                return "/product/viewProduct";
            }
            List<ProductResponseDto> products = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                    .filter(product ->
                            (product.getName() != null && product.getName().toLowerCase().contains(query.toLowerCase())) ||
                                    (product.getCode() != null && product.getCode().contains(query)) ||
                                    (product.getVolume() != null && product.getVolume().toLowerCase().contains(query.toLowerCase()))
                    )
                    .map(productMapper::toResponseDto)
                    .toList();


            if(products == null){
                model.addAttribute("error", "Товар не найден");
                return "/product/viewProduct";
            }

            model.addAttribute("allProducts", products);
            return "/product/viewProduct";
        } catch (Exception e) {
            logger.error("Ошибка при поиске товара", e);
            model.addAttribute("errorMessage", "Ошибка при поиске товара");
            return "error/error";
        }
    }
}
