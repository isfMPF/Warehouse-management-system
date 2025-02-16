package com.project.wms.service;

import com.project.wms.dto.requestdto.ProductRequestDto;
import com.project.wms.entity.ClientEntity;
import com.project.wms.entity.ProductEntity;
import com.project.wms.mapper.ProductMapper;
import com.project.wms.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    public ProductRepository productRepository;
    @Autowired
    public ProductMapper productMapper;

    public Iterable<ProductEntity> getAllProducts(){
        return productRepository.findAll();
    }

    public void addProduct(ProductRequestDto productRequestDto){
        ProductEntity entity = productMapper.toEntity(productRequestDto);
        productRepository.save(entity);
    }

    public void deteleProduct(Long id){
        productRepository.deleteById(id);
    }

    public ProductEntity getProductsByNameIgnoreCaseOrByCode(String query) {
        if (query.matches("\\d+")){
            return productRepository.findByCode(query);
        }
        return productRepository.findByNameIgnoreCase(query);
    }

    public ProductEntity getProductByCode(String code){
        return productRepository.findByCode(code);
    }


}
