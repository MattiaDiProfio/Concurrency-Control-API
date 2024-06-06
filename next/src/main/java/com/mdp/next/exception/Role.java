package com.mdp.next.exception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RoleValidator.class)
public @interface Role {

    //default message if constraint is violated
    String message() default "User role must be of type 'BASE' or 'ADMIN'";
    //boilerplate parameters.
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
