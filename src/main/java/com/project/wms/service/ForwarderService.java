package com.project.wms.service;

import com.project.wms.dto.requestdto.ForwarderRequestDto;
import com.project.wms.dto.responsedto.ForwarderResponseDto;
import com.project.wms.mapper.ForwarderMapper;
import com.project.wms.repository.ForwarderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ForwarderService {

    @Autowired
    private ForwarderRepository forwarderRepository;
    @Autowired
    private ForwarderMapper forwarderMapper;


    public List<ForwarderResponseDto> findAll(){
        return forwarderMapper.toResponseDto(forwarderRepository.findAll());
    }

    public void save(ForwarderRequestDto forwarderRequestDto){
        forwarderRepository.save(forwarderMapper.toEntity(forwarderRequestDto));
    }

    public Optional<ForwarderResponseDto> findById(Long id) {
        return forwarderRepository.findById(id)
                .map(forwarderMapper::toResponseDto);
    }

    public void delete(Long id){
        forwarderRepository.deleteById(id);
    }

}
