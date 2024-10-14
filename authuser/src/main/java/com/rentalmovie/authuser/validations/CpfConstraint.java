package com.rentalmovie.authuser.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CpfConstraintImpl.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CpfConstraint {
    String message() default "CPF invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
