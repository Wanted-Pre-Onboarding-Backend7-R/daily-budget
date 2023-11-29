package com.mojh.dailybudget.common.vaildation.validator;

import com.mojh.dailybudget.common.dto.request.PeriodRequest;
import com.mojh.dailybudget.common.vaildation.annotation.ValidPeriodRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;


public class PeriodRequestValidator implements ConstraintValidator<ValidPeriodRequest, PeriodRequest> {

    private ValidPeriodRequest annotation;

    @Override
    public void initialize(ValidPeriodRequest constraintAnnotation) {
        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(PeriodRequest value, ConstraintValidatorContext context) {
        LocalDateTime beginAt = value.getBeginDate();
        LocalDateTime endAt = value.getEndDate();

        if (!annotation.isNullable() && (beginAt == null || endAt == null)) {
            return false;
        }

        // 시작 일시 <= 종료 일시 확인
        return beginAt.isBefore(endAt) || beginAt.isEqual(endAt);
    }

}