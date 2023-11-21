package com.mojh.dailybudget.common.vaildation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        return value != null;
    }

}