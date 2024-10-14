package com.rentalmovie.authuser.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CpfConstraintImpl implements ConstraintValidator<CpfConstraint, String> {
    @Override
    public void initialize(CpfConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext constraintValidatorContext) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String methodType = attributes.getRequest().getMethod();
            if (methodType.equals("POST") && cpf.isBlank()) {
                return true;
            }
        }

        if (cpf.length() != 11 || cpf.isBlank()) {
            return false;
        }
        if(cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int firstVerifyDigit = Integer.parseInt(cpf.substring(9, 10));
        int secondVerifyDigit = Integer.parseInt(cpf.substring(10));

        return firstVerifyDigit == validCpf(cpf, 10) && secondVerifyDigit == validCpf(cpf, 11);
    }

    private int validCpf(String cpf, int numberMultiply) {
        int sum = 0;
        int forSize = numberMultiply - 1;

        for (int i = 0; i < forSize; i++) {
            int num = Integer.parseInt(cpf.substring(i, i + 1));
            sum += num * numberMultiply--;
        }

        int result = sum % 11;

        return (result < 2) ? 0 : 11 - result;
    }
}
