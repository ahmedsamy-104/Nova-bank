package com.Novabank.common.validation;

import com.Novabank.Customer.Dto.RegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegistrationRequestDto> {

    @Override
    public boolean isValid(RegistrationRequestDto dto, ConstraintValidatorContext context) {

        boolean matches = dto.getPassword().equals(dto.getConfirmPassword());
        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }
        return matches;
    }
}