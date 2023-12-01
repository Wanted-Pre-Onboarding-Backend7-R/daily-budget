package com.mojh.dailybudget.expenditure.validation;

import com.mojh.dailybudget.expenditure.dto.request.ExpenditureListRetrieveRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;


public class ExpenditureListRetrieveRequestValidator
        implements ConstraintValidator<ValidExpenditureListRetrieveRequest, ExpenditureListRetrieveRequest> {

    @Override
    public boolean isValid(ExpenditureListRetrieveRequest value, ConstraintValidatorContext context) {
        LocalDateTime beginDate = value.getBeginDate();
        LocalDateTime endDate = value.getEndDate();

        // 기간 정보는 필수이므로 null이거나 종료 일시 < 시작 일시 이면 false
        if (beginDate == null || endDate == null || endDate.isBefore(beginDate)) {
            return false;
        }

        // 최대 금액 < 최소 금액이면 false
        if (value.getMaxAmount() < value.getMinAmount()) {
            return false;
        }

        return true;
    }

}