package com.project.wms.service;

import com.project.wms.dto.requestdto.ClientRequestDto;
import com.project.wms.entity.ClientEntity;
import com.project.wms.mapper.ClientMapper;
import com.project.wms.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService  {

    @Autowired
    public ClientRepository clientRepository;
    @Autowired
    public ClientMapper clientMapper;

    public Iterable<ClientEntity> getAllClients(){
        return clientRepository.findAll();
    }


    public void addClient(ClientRequestDto clientRequestDto){
        ClientEntity entity = clientMapper.toEntity(clientRequestDto);
        clientRepository.save(entity);
    }

    public void deteleClient(Long id){
        clientRepository.deleteById(id);
    }

    public ClientEntity getClientsByNameIgnoreCaseOrByCodeClient(String query) {
        if (query.matches("\\d+")){
            return clientRepository.findByCodeClient(Long.parseLong(query));
        }
        return clientRepository.findByNameIgnoreCase(query);
    }




}
