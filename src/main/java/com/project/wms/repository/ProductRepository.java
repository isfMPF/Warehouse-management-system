package com.project.wms.repository;

import com.project.wms.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity,Long> {

    List<ProductEntity> findByNameIgnoreCase(String name);
    List<ProductEntity> findByCode(String code);

}
