package com.mojh.dailybudget.common.vaildation;

import com.mojh.dailybudget.category.domain.CategoryType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Stream;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private ValidEnum annotation;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 필드가 null일 때 null을 허용하지 않으면 false, 허용하면 true
        if(value == null) {
            return annotation.allowsNull();
        }

        // 대소문자 구분 여부에 따라 처리
        if (!annotation.caseSensitive()) {
            value = value.toUpperCase();
        }

        // 필드가 null이 아닐 때 enum 필드와 매칭되는지 확인
        String finalValue = value;
        return Arrays.stream(annotation.enumClass().getEnumConstants())
                     .anyMatch(enumValue -> enumValue.name().equals(finalValue));
    }

}