package com.project.wms.repository;

import com.project.wms.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ProductRepository extends JpaRepository<ProductEntity,Long> {

    ProductEntity findByNameIgnoreCase(String name);
    ProductEntity findByCode(String code);
    // Метод для поиска множества продуктов по кодам
    Set<ProductEntity> findByCodeIn(Set<String> codes);


}
