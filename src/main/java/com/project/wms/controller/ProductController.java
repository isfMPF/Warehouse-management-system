package com.project.wms.controller;

import com.project.wms.dto.requestdto.ProductArrivalDto;
import com.project.wms.dto.requestdto.ProductRequestDto;
import com.project.wms.dto.responsedto.ClientResponseDto;
import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.entity.ProductEntity;
import com.project.wms.mapper.ProductMapper;
import com.project.wms.repository.ProductRepository;
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
    public ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService, ProductMapper productMapper, ProductRepository productRepository) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @GetMapping("/products")
    public String showAllProducts(Model model){

        try {
            List<ProductResponseDto> productResponseDtos = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                    .map(productMapper::toResponseDto)
                    .toList();

            model.addAttribute("allProducts", productResponseDtos);
            int totalAmount = productRepository.findAll().stream().mapToInt(ProductEntity::getAmount).sum();
            model.addAttribute("totalAmount", totalAmount);
            return "product/viewProduct";
        }catch (Exception e) {
            logger.error("Ошибка при загрузке страницы", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке страницы");
            return "error/error";
        }

    }

    @GetMapping("/form-product")
    public String showFormsAddProduct(Model model){

        try {
            model.addAttribute("productRequestDto", new ProductRequestDto());
            return "product/addProduct";
        } catch (Exception e) {
            logger.error("Ошибка при загрузке страницы", e);
            model.addAttribute("errorMessage", "Ошибка при загрузке страницы");
            return "error/error";
        }


    }

    @PostMapping("/add-product")
    public String addClient(@ModelAttribute("productRequestDto") @Valid ProductRequestDto productRequestDto,
                            BindingResult errors, Model model){

        try {
            if(errors.hasErrors())
            {
                return "product/addProduct";
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
            ProductEntity productEntity = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Товар не найден"));

            ProductResponseDto productResponseDto = productMapper.toResponseDto(productEntity);
            model.addAttribute("product", productResponseDto);
            model.addAttribute("productRequestDto", productMapper.toRequestDto(productEntity));
            return "product/editProduct";
        } catch (Exception e) {
            logger.error("Ошибка при редактировании товара", e);
            model.addAttribute("errorMessage", e.getMessage() + " Ошибка при редактировании товара");
            return "error/error";
        }
    }

    @PostMapping("/edit-product")
    public String editClient(@ModelAttribute("productRequestDto") @Valid ProductRequestDto productRequestDto,
                             BindingResult errors, Model model) {
        try {
            if(errors.hasErrors()) {
                // Если есть ошибки, возвращаемся на форму с текущими данными
                ProductResponseDto productResponseDto = productMapper.toResponseDto(
                        productRepository.findById(productRequestDto.getId()).orElseThrow()
                );
                model.addAttribute("product", productResponseDto);
                return "product/editProduct";
            }

            productService.updateProduct(productRequestDto);
            return "redirect:/products";
        } catch (Exception e) {
            logger.error("Ошибка при редактировании товара", e);
            model.addAttribute("errorMessage", "Ошибка при редактировании товара: " + e.getMessage());
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
                return "product/viewProduct";
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
                return "product/viewProduct";
            }

            model.addAttribute("allProducts", products);
            return "product/viewProduct";
        } catch (Exception e) {
            logger.error("Ошибка при поиске товара", e);
            model.addAttribute("errorMessage", "Ошибка при поиске товара");
            return "error/error";
        }
    }

    @GetMapping("/products/arrival")
    public String arrivalProduct(Model model) {
        try {
            List<ProductResponseDto> productResponseDtos = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                    .map(productMapper::toResponseDto)
                    .toList();

            model.addAttribute("products", productResponseDtos);
            model.addAttribute("productArrivalDto", new ProductArrivalDto()); // Используем новый DTO
            return "product/arrival";
        } catch (Exception e) {
            logger.error("Ошибка при приходе товара", e);
            model.addAttribute("errorMessage", "Ошибка при приходе товара");
            return "error/error";
        }
    }

    @PostMapping("/products/arrival")
    public String updateAmount(@ModelAttribute("productArrivalDto") @Valid ProductArrivalDto productArrivalDto,
                               BindingResult errors, Model model) {
        try {
            if(errors.hasErrors()) {
                // Нужно снова добавить список товаров, так как при ошибке форма будет показана снова
                List<ProductResponseDto> productResponseDtos = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                        .map(productMapper::toResponseDto)
                        .toList();
                model.addAttribute("products", productResponseDtos);
                return "product/arrival";
            }

            productService.increaseProductAmount(productArrivalDto.getCode(), productArrivalDto.getAmountToAdd());
            return "redirect:/products?success";
        } catch (Exception e) {
            logger.error("Ошибка при приходе товара", e);
            model.addAttribute("errorMessage", "Ошибка при приходе товара: " + e.getMessage());
            return "error/error";
        }
    }


}
