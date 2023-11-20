package com.mojh.dailybudget.common.vaildation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {

    private ValidEnum annotation;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Enum value, ConstraintValidatorContext context) {
        Enum<?>[] enumValues = this.annotation.enumClass().getEnumConstants();
        if(value == null || enumValues == null) {
            return false;
        }

        for (Enum<?> enumValue : enumValues) {
            if (enumValue.equals(value)) {
                return true;
            }
        }
        return false;
    }

}