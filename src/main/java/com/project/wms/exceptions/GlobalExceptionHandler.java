package com.project.wms.exceptions;

import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.slf4j.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =  LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        // Логируем полную ошибку
        logger.error("Произошла ошибка: ", ex);

        // Добавляем в модель простое сообщение для пользователя
        model.addAttribute("errorMessage", "Произошла ошибка. Пожалуйста, попробуйте позже.");
        return "error/error";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        logger.error("Runtime exception: ", ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }
}
