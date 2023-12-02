package com.mojh.dailybudget.budget.dto;

import com.mojh.dailybudget.budget.domain.BudgetCategory;
import com.mojh.dailybudget.budget.validation.ValidBudgetCategoryRequests;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.common.vaildation.ValidEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BudgetPutRequest {

    // TODO: 적절한 년도 설정

    @Range(min = 2000, max = 2100)
    private Integer year;

    @Range(min = 1, max = 12)
    private Integer month;

    @Valid
    @ValidBudgetCategoryRequests
    private List<BudgetCategoryRequest> budgetCategoryRequests;

    public BudgetPutRequest(Integer year, Integer month, List<BudgetCategoryRequest> budgetCategoryRequests) {
        this.year = year;
        this.month = month;
        this.budgetCategoryRequests = budgetCategoryRequests;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BudgetCategoryRequest {

        @ValidEnum(enumClass = CategoryType.class)
        private String category;

        // TODO: max값 적절한 값으로 설정
        @Range(min = 0L, max = 1000000000000L)
        private long amount;

        public BudgetCategoryRequest(String category, long amount) {
            this.category = category;
            this.amount = amount;
        }

        public BudgetCategory toEntity() {
            return BudgetCategory.of(CategoryType.valueOf(category), amount);
        }

        // equals, hashcode ide 자동 생성 이용한 코드
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BudgetCategoryRequest)) return false;
            BudgetCategoryRequest that = (BudgetCategoryRequest) o;
            return category == that.category;
        }

        @Override
        public int hashCode() {
            return Objects.hash(category);
        }

    }

}
