package com.project.wms.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SelectedItemsValidator.class)
public @interface ValidSelectedItem {

     String message() default "Некорректные данные для выбранных товаров";
     Class<?>[] groups() default {};
     Class<? extends Payload>[] payload() default {};

}
