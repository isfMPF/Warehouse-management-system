package com.project.wms.service;

import com.project.wms.dto.requestdto.ProductRequestDto;
import com.project.wms.entity.ClientEntity;
import com.project.wms.entity.ProductEntity;
import com.project.wms.mapper.ProductMapper;
import com.project.wms.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
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


    public void increaseProductAmount(String code, int amountToAdd) {
        ProductEntity product = productRepository.findByCode(code);
        product.setAmount(product.getAmount() + amountToAdd);
        productRepository.save(product);
    }

    public void updateProduct(ProductRequestDto productRequestDto) {
        // Загружаем существующую сущность
        ProductEntity existingEntity = productRepository.findById(productRequestDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Обновляем поля, кроме version
        existingEntity.setCode(productRequestDto.getCode());
        existingEntity.setName(productRequestDto.getName());
        existingEntity.setVolume(productRequestDto.getVolume());
        existingEntity.setUnit(productRequestDto.getUnit());
        existingEntity.setQuantity(productRequestDto.getQuantity());
        existingEntity.setPrice(productRequestDto.getPrice());
        existingEntity.setAmount(productRequestDto.getAmount());
        existingEntity.setWeight(productRequestDto.getWeight());

        productRepository.save(existingEntity);
    }
}
