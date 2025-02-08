package com.project.wms.repository;

import com.project.wms.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClientRepository extends JpaRepository<ClientEntity,Long> {

    ClientEntity findByNameIgnoreCase(String name);
    ClientEntity findByCodeClient(Long code);

}
