package com.project.wms.controller;

import com.project.wms.dto.requestdto.ClientRequestDto;
import com.project.wms.entity.ClientEntity;
import com.project.wms.repository.ClientRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.AttributedString;

@Controller
@RequestMapping("/clients")
public class ClientController {

 @GetMapping
    public String showClients(Model model){
     model.addAttribute("clientRequestDto", new ClientRequestDto());
     return "client";
 }

 @PostMapping
    public String addProduct(@ModelAttribute("clientRequestDto") @Valid ClientRequestDto clientRequestDto,
                               BindingResult errors){
      
     if(errors.hasErrors()){
         return "client";
     }

     return "redirect:/clients?success";
 }
}
