package com.project.wms.controller;

import com.project.wms.dto.requestdto.ClientRequestDto;
import com.project.wms.dto.requestdto.ProductRequestDto;
import com.project.wms.dto.responsedto.ClientResponseDto;
import com.project.wms.dto.responsedto.ProductResponseDto;
import com.project.wms.mapper.ProductMapper;
import com.project.wms.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@Controller
public class ProductController {
    public ProductService productService;
    public ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping("/products")
    public String showAllProducts(Model model){

        List<ProductResponseDto> productResponseDtos = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                .map(productMapper::toResponseDto)
                .toList();

        model.addAttribute("allProducts", productResponseDtos);
        return "/product/viewProduct";
    }

    @GetMapping("/form-product")
    public String showFormsAddProduct(Model model){

        model.addAttribute("productRequestDto", new ProductRequestDto());
        return "/product/addProduct";

    }

    @PostMapping("/add-product")
    public String addClient(@ModelAttribute("productRequestDto") @Valid ProductRequestDto productRequestDto,
                            BindingResult errors){

        if(errors.hasErrors())
        {
            return "/product/addProduct";
        }

        productService.addProduct(productRequestDto);
        return "redirect:/products";

    }

    @GetMapping("/productEdit/{id}")
    public String Edit(@PathVariable(value = "id") long id, Model model) {

        ProductResponseDto productResponseDto = StreamSupport.stream(productService.getAllProducts().spliterator(), false)
                .map(productMapper::toResponseDto)
                .filter(product -> product.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        model.addAttribute("product", productResponseDto);
        return "/product/editProduct";

    }




    @GetMapping("/productDelete/{id}")
    public String deleteClient(@PathVariable(value = "id") long id){

        productService.deteleProduct(id);
        return "redirect:/products";

    }

    @GetMapping("/search-product")
    public String showProductByNameOrByCode(@RequestParam String query, Model model){

        if (query == null || query.trim().isEmpty()) {
            model.addAttribute("error", "Введите название для поиска.");
            return "/product/viewProduct";
        }

        List<ProductResponseDto> products = productService.getProductsByNameIgnoreCaseOrByCode(query).stream()
                .map(productMapper::toResponseDto)
                .toList();

        if(products.isEmpty()){
            model.addAttribute("error", "Товар не найден");
            return "/product/viewProduct";
        }

        model.addAttribute("allProducts", products);
        return "/product/viewProduct";

    }
}
