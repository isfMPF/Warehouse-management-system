package com.project.wms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login(Model model) {
        try {
            return "login/login";
        } catch (Exception e) {
            logger.error("Ошибка при входе", e);
            model.addAttribute("errorMessage", "Ошибка при входе");
            return "error/error";
        }

    }
}
