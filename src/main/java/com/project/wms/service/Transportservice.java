package com.project.wms.service;

import com.project.wms.dto.requestdto.TransportRequestDto;
import com.project.wms.dto.responsedto.TransportResponseDto;
import com.project.wms.mapper.TransportMapper;
import com.project.wms.repository.TransportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Transportservice {

    @Autowired
    private TransportRepository transportRepository;
    @Autowired
    private TransportMapper transportMapper;

    public List<TransportResponseDto> findAll(){
        return transportMapper.toResponseDTO(transportRepository.findAll());
    }

    public void save(TransportRequestDto transportRequestDto){
       transportRepository.save(transportMapper.toEntity(transportRequestDto));
    }

    public Optional<TransportResponseDto> findById(Long id) {
        return transportRepository.findById(id)
                .map(transportMapper::toResponseDto);
    }

    public void delete(Long id){
        transportRepository.deleteById(id);
    }
}
