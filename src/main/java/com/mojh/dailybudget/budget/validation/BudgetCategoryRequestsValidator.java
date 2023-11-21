package com.mojh.dailybudget.budget.validation;

import com.mojh.dailybudget.budget.dto.BudgetPutRequest.BudgetCategoryRequest;
import com.mojh.dailybudget.category.domain.CategoryType;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class BudgetCategoryRequestsValidator
        implements ConstraintValidator<ValidBudgetCategoryRequests, List<BudgetCategoryRequest>> {

    private Set<CategoryType> enumSet;

    @Override
    public void initialize(ValidBudgetCategoryRequests constraintAnnotation) {
        enumSet = Arrays.stream(CategoryType.values())
                        .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(List<BudgetCategoryRequest> value, ConstraintValidatorContext context) {
        if (value.size() != enumSet.size()) {
            return false;
        }

        Set<CategoryType> checked = new HashSet<>();

        for (BudgetCategoryRequest budgetCategoryRequest : value) {
            CategoryType currentType = budgetCategoryRequest.getCategory();

            // 입력된 카테고리별 예산 필드 중복 확인
            if (checked.contains(currentType)) {
                return false;
            }
            checked.add(currentType);

            if (!enumSet.contains(budgetCategoryRequest.getCategory())) {
                return false;
            }
        }

        return true;
    }

}
