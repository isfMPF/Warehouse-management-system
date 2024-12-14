package com.project.wms.controller;

import com.project.wms.mapper.ClientMapper;
import com.project.wms.mapper.ProductMapper;
import com.project.wms.repository.ClientRepository;
import com.project.wms.repository.ProductRepository;
import com.project.wms.service.ClientService;
import com.project.wms.service.OrderService;
import com.project.wms.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;




}
