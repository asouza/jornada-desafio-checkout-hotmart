package com.deveficiente.desafiocheckouthotmart.compartilhado;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

//#chatgptGerou
public class FutureOrPresentYearValidator implements ConstraintValidator<FutureOrPresentYear, Integer> {

    @Override
    public void initialize(FutureOrPresentYear constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
        	//seguindo o padrao da Bean Validation e sendo bem especifico quanto a validacao
            return true; 
        }

        int currentYear = LocalDate.now().getYear();
        return value >= currentYear;
    }
}