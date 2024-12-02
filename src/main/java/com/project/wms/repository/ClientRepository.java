package com.project.wms.repository;

import com.project.wms.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<ClientEntity,Long> {

    List<ClientEntity> findByNameIgnoreCase(String name);

}
