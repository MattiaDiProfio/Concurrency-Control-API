package com.mdp.next.exception;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleValidator implements ConstraintValidator<Role, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (value.equals("BASE") || value.equals("ADMIN"));
    }
    
}
