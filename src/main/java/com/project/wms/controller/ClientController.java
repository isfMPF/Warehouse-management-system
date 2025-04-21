package com.project.wms.controller;

import com.project.wms.dto.requestdto.ClientRequestDto;
import com.project.wms.dto.responsedto.ClientResponseDto;
import com.project.wms.mapper.ClientMapper;
import com.project.wms.service.ClientService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/clients")
public class ClientController {

    public final ClientService clientService;
    public final ClientMapper clientMapper;
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);


    public ClientController(ClientService clientService, ClientMapper clientMapper) {
        this.clientService = clientService;
        this.clientMapper = clientMapper;
    }

    @GetMapping
    public String showAllClients(Model model) {

        try{
            List<ClientResponseDto> clientDtos = StreamSupport.stream(clientService.getAllClients().spliterator(), false)
                    .map(clientMapper::toResponseDto)
                    .collect(Collectors.toList());


            model.addAttribute("allClients", clientDtos);
            return "client/viewClients";

        } catch (Exception e) {
            logger.error("Ошибка при загрузке списков клиентов", e);
            model.addAttribute("errorMessage", "Не удалось загрузить данные клиентов");
            return "error/error";
        }
    }
    // Вспомогательный метод
    private String getRussianDayName(DayOfWeek day) {
        switch (day) {
            case MONDAY:    return "понедельник";
            case TUESDAY:   return "вторник";
            case WEDNESDAY: return "среда";
            case THURSDAY:  return "четверг";
            case FRIDAY:   return "пятница";
            case SATURDAY:  return "суббота";
            default:        return "";
        }
    }

    @GetMapping("/search")
    public String showClientByName(@RequestParam String query,Model model){

        try {
            if (query == null || query.trim().isEmpty()) {
                model.addAttribute("error", "Введите название для поиска.");
                return "client/viewClients";
            }

            List<ClientResponseDto> clients = StreamSupport.stream(clientService.getAllClients().spliterator(), false)
                    .filter(client ->
                            (client.getName() != null && client.getName().toLowerCase().contains(query.toLowerCase())) ||
                                    (client.getDayOfWeek() != null &&
                                            getRussianDayName(client.getDayOfWeek()).toLowerCase().contains(query.toLowerCase())) ||
                                    (client.getCodeClient() != null && String.valueOf(client.getCodeClient()).contains(query)) ||
                                    (client.getAddress() != null && client.getAddress().toLowerCase().contains(query.toLowerCase()))
                    )
                    .map(clientMapper::toResponseDto)
                    .collect(Collectors.toList());
            if(clients == null){
                model.addAttribute("error", "Клиент не найден");
                return "client/viewClients";
            }

            model.addAttribute("allClients", clients);
            return "client/viewClients";
        } catch (Exception e) {
            logger.error("Ошибка при поиске клиента", e);
            model.addAttribute("errorMessage", "При поиске не удалось загрузить данные клиента");
            return "error/error";
        }
    }


    @GetMapping("/form-client")
    public String showFormsAddClient(Model model){

        try {
            model.addAttribute("clientRequestDto", new ClientRequestDto());
            return "client/addClient";
        } catch (Exception e) {
            logger.error("Ошибка при переходе на страницу Добавить клиент", e);
            model.addAttribute("errorMessage", "Ошибка при переходе на страницу 'Добавить клиент'");
            return "error/error";
        }
    }

    @PostMapping("/add-client")
    public String addClient(@ModelAttribute("clientRequestDto") @Valid ClientRequestDto clientRequestDto,
                               BindingResult errors, Model model){

        try{
            if(errors.hasErrors())
            {
                return "client/addClient";
            }

           clientService.addClient(clientRequestDto);
            return "redirect:/clients";
        } catch (Exception e) {
            logger.error("Ошибка при добавлении клиента", e);
            model.addAttribute("errorMessage", "Ошибка при добавлении клиента");
            return "error/error";
        }
    }


    @GetMapping("/clientEdit/{id}")
    public String Edit(@PathVariable(value = "id") long id, Model model) {
        try {
            ClientResponseDto clientDto = StreamSupport.stream(clientService.getAllClients().spliterator(), false)
                    .map(clientMapper::toResponseDto)
                    .filter(client -> client.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Клиент не найден"));

            model.addAttribute("client", clientDto);
            model.addAttribute("clientRequestDto", new ClientRequestDto());
            return "client/editClient";

        } catch (Exception e) {
            logger.error("Ошибка при редактировании клиента", e);
            model.addAttribute("errorMessage", "Ошибка при редактировании клиента");
            return "error/error";
        }
    }


    @PostMapping("/edit-client")
    public String editClient(@ModelAttribute("client") @Valid ClientRequestDto clientRequestDto,
                            BindingResult errors, Model model){
        try {
            if(errors.hasErrors())
            {
                return "client/editClient";
            }

            clientService.addClient(clientRequestDto);
            return "redirect:/clients";
        } catch (Exception e) {
            logger.error("Ошибка при редактировании клиента (Method POST)", e);
            model.addAttribute("errorMessage", "Ошибка при редактировании клиента");
            return "error/error";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/clientDelete/{id}")
    public String deleteClient(@PathVariable(value = "id") long id, Model model){
        try{
            clientService.deteleClient(id);
            return "redirect:/clients";
        } catch (Exception e) {
            logger.error("Ошибка при удалении клиента", e);
            model.addAttribute("errorMessage", "Ошибка при удалении клиента");
            return "error/error";
        }
    }

}
