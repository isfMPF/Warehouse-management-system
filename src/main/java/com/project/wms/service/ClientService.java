package com.project.wms.service;

import com.project.wms.dto.requestdto.ClientRequestDto;
import com.project.wms.entity.ClientEntity;
import com.project.wms.mapper.ClientMapper;
import com.project.wms.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<ClientEntity> getClientsByNameIgnoreCase(String name) {
        return clientRepository.findByNameIgnoreCase(name);
    }


}
