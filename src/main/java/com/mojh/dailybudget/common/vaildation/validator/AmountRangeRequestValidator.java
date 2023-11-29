package com.mojh.dailybudget.common.vaildation.validator;

import com.mojh.dailybudget.common.dto.request.AmountRangeRequest;
import com.mojh.dailybudget.common.vaildation.annotation.ValidAmountRangeRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AmountRangeRequestValidator implements ConstraintValidator<ValidAmountRangeRequest, AmountRangeRequest> {

    @Override
    public boolean isValid(AmountRangeRequest value, ConstraintValidatorContext context) {
        return value.getMinAmount() <= value.getMaxAmount();
    }

}
