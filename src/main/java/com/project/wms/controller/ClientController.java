package com.project.wms.controller;

import com.project.wms.dto.requestdto.ClientRequestDto;
import com.project.wms.dto.responsedto.ClientResponseDto;
import com.project.wms.mapper.ClientMapper;
import com.project.wms.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class ClientController {

    public final ClientService clientService;
    public final ClientMapper clientMapper;

    public ClientController(ClientService clientService, ClientMapper clientMapper) {
        this.clientService = clientService;
        this.clientMapper = clientMapper;
    }

    @GetMapping("/clients")
    public String showAllClients(Model model) {

        List<ClientResponseDto> clientDtos = StreamSupport.stream(clientService.getAllClients().spliterator(), false)
                .map(clientMapper::toResponseDto)
                .collect(Collectors.toList());

        model.addAttribute("allClients", clientDtos);
        return "/client/viewClients";

    }

    @GetMapping("/search")
    public String showClientByName(@RequestParam String query,Model model){

        if (query == null || query.trim().isEmpty()) {
            model.addAttribute("error", "Введите название для поиска.");
            return "/client/viewClients";
        }

        List<ClientResponseDto> clients = StreamSupport.stream(clientService.getAllClients().spliterator(), false)
                .filter(client ->
                        (client.getName() != null && client.getName().toLowerCase().contains(query.toLowerCase())) ||
                                (client.getCodeClient() != null && String.valueOf(client.getCodeClient()).contains(query)) ||
                                (client.getAddress() != null && client.getAddress().toLowerCase().contains(query.toLowerCase()))
                        )
                .map(clientMapper::toResponseDto)
                .collect(Collectors.toList());
        if(clients == null){
            model.addAttribute("error", "Клиент не найден");
            return "/client/viewClients";
        }

        model.addAttribute("allClients", clients);
        return "/client/viewClients";

    }


    @GetMapping("/form-client")
    public String showFormsAddClient(Model model){

        model.addAttribute("clientRequestDto", new ClientRequestDto());
        return "/client/addClient";

    }

    @PostMapping("/add-client")
    public String addClient(@ModelAttribute("clientRequestDto") @Valid ClientRequestDto clientRequestDto,
                               BindingResult errors){

        if(errors.hasErrors())
        {
            return "/client/addClient";
        }

        clientService.addClient(clientRequestDto);
        return "redirect:/clients";

    }


    @GetMapping("/clientEdit/{id}")
    public String Edit(@PathVariable(value = "id") long id, Model model) {

        ClientResponseDto clientDto = StreamSupport.stream(clientService.getAllClients().spliterator(), false)
                .map(clientMapper::toResponseDto)
                .filter(client -> client.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

            model.addAttribute("client", clientDto);
            return "/client/editClient";

    }



    @GetMapping("/clientDelete/{id}")
    public String deleteClient(@PathVariable(value = "id") long id){

        clientService.deteleClient(id);
        return "redirect:/clients";

    }

}
