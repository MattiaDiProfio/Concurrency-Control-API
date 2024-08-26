package com.mdp.next.exception;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TypeValidator implements ConstraintValidator<Type, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String[] validTypes = { "SAVINGS", "CHECKING", "INVESTMENT" };
        for(String type : validTypes) {
            if(type.equals(value)) return true;
        }
        return false;
    }

}

