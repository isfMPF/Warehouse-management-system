package com.project.wms.controller;

import com.project.wms.dto.requestdto.ClientRequestDto;
import com.project.wms.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ClientController {


    public final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/clients")
    public String showAllClients(){
        return "viewClients";
    }


    @GetMapping("/form-client")
    public String showFormsAddClient(Model model){
     model.addAttribute("clientRequestDto", new ClientRequestDto());
     return "addClient";
 }

 @PostMapping("/add-client")
    public String addClient(@ModelAttribute("clientRequestDto") @Valid ClientRequestDto clientRequestDto,
                               BindingResult errors){
      
     if(errors.hasErrors()){
         return "addClient";
     }
    clientService.addClient(clientRequestDto);

     return "redirect:/addClient?success";
 }
}
