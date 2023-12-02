package com.mojh.dailybudget.budget.service;

import com.mojh.dailybudget.budget.domain.Budget;
import com.mojh.dailybudget.budget.domain.BudgetCategory;
import com.mojh.dailybudget.budget.dto.BudgetPutRequest;
import com.mojh.dailybudget.budget.dto.BudgetPutRequest.BudgetCategoryRequest;
import com.mojh.dailybudget.budget.repository.BudgetRepository;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.common.exception.DailyBudgetAppException;
import com.mojh.dailybudget.common.web.dto.PutResultResponse;
import com.mojh.dailybudget.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mojh.dailybudget.common.exception.ErrorCode.TOTAL_BUDGET_LIMIT_EXCESS;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final Long MAX_TOTAL_BUDGET_AMOUNT = 1000000000000L; // 1조

    public BudgetService(final BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public PutResultResponse putBudget(BudgetPutRequest request, Member member) {
        Long totalAmount = request.getBudgetCategoryRequests().stream()
                                  .mapToLong(budget -> budget.getAmount())
                                  .sum();

        // 총 예산 최대 설정 금액 체크
        if (totalAmount > MAX_TOTAL_BUDGET_AMOUNT) {
            throw new DailyBudgetAppException(TOTAL_BUDGET_LIMIT_EXCESS);
        }

        Optional<Budget> optionalBudget =
                budgetRepository.findByMemberAndBudgetYearAndBudgetMonth(member, request.getYear(), request.getMonth());

        // replace
        if (optionalBudget.isPresent()) {
            Budget budget = optionalBudget.get();

            // 카테고리별 예산 list -> map
            Map<CategoryType, BudgetCategoryRequest> budgetCategoryRequests =
                    request.getBudgetCategoryRequests().stream()
                           .collect(Collectors.toMap(req -> CategoryType.valueOf(req.getCategory()) , Function.identity()));

            // 카테고리별 예산 update
            for (BudgetCategory budgetCategory : budget.getBudgetCategoryList()) {
                CategoryType categoryType = budgetCategory.getCategoryType();
                budgetCategory.updateAmount(budgetCategoryRequests.get(categoryType).getAmount());
            }

            return PutResultResponse.replaced(budget.getId());
        }

        // create
        List<BudgetCategory> budgetCategoryList = request.getBudgetCategoryRequests().stream()
                                                         .map(BudgetCategoryRequest::toEntity)
                                                         .collect(Collectors.toList());

        Budget budget = Budget.builder()
                              .member(member)
                              .budgetYear(request.getYear())
                              .budgetMonth(request.getMonth())
                              .totalAmount(totalAmount)
                              .budgetCategoryList(budgetCategoryList)
                              .build();
        budgetRepository.save(budget);

        return PutResultResponse.created(budget.getId());
    }

}
