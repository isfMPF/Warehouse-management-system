package com.project.wms.service;

import com.project.wms.mapper.OrderItemMapper;
import com.project.wms.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderItemService {

    @Autowired
    public OrderItemRepository orderItemRepository;
    @Autowired
    public OrderItemMapper orderItemMapper;

}
