package com.project.wms.util.validation;

import com.project.wms.dto.requestdto.OrderItemRequestDto;
import com.project.wms.dto.requestdto.OrderRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class SelectedItemsValidator implements ConstraintValidator<ValidSelectedItem, OrderRequestDto> {

    @Override
    public void initialize(ValidSelectedItem constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(OrderRequestDto orderRequest, ConstraintValidatorContext context) {
        List<OrderItemRequestDto> items = orderRequest.getItems();
        if (items == null || items.isEmpty()) {
            return true; // Если список пустой, валидация не требуется
        }

        // Проверка, что хотя бы один товар выбран
        boolean atLeastOneSelected = items.stream().anyMatch(OrderItemRequestDto::isSelected);
        if (!atLeastOneSelected) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Выберите хотя бы один товар")
                    .addPropertyNode("items") // Указываем, что ошибка относится к списку товаров
                    .addConstraintViolation();
            return false; // Возвращаем false, если ни один товар не выбран
        }

        // Проверка выбранных товаров
        boolean isValid = true;
        for (int i = 0; i < items.size(); i++) {
            OrderItemRequestDto item = items.get(i);
            if (item.isSelected()) { // Проверяем только выбранные товары
                if (item.getCode() == null || item.getCode().isEmpty()) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Введите код товара для выбранного товара")
                            .addPropertyNode("items[" + i + "].code")
                            .addConstraintViolation();
                    isValid = false;
                }
                if (item.getAmount() <= 0) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Количество товара должно быть больше 0")
                            .addPropertyNode("items[" + i + "].amount")
                            .addConstraintViolation();
                    isValid = false;
                }
            }
        }

        return isValid;
    }
}
