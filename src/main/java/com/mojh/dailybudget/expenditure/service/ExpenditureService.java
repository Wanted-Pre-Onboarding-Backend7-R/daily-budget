package com.mojh.dailybudget.expenditure.service;

import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.category.service.CategorySerivce;
import com.mojh.dailybudget.common.exception.DailyBudgetAppException;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureCreateRequest;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureListRetrieveRequest;
import com.mojh.dailybudget.expenditure.dto.response.ExpenditureListResponse;
import com.mojh.dailybudget.expenditure.dto.response.ExpenditureSummaryResponse;
import com.mojh.dailybudget.expenditure.dto.request.ExpenditureUpdateRequest;
import com.mojh.dailybudget.expenditure.repository.ExpenditureRepository;
import com.mojh.dailybudget.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mojh.dailybudget.common.exception.ErrorCode.EXPENDITURE_MEMBER_MISMATCH;
import static com.mojh.dailybudget.common.exception.ErrorCode.EXPENDITURE_NOT_FOUND;

@Service
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    private final CategorySerivce categoryService;

    public ExpenditureService(final ExpenditureRepository expenditureRepository,
                              final CategorySerivce categoryService) {
        this.expenditureRepository = expenditureRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public Long createExpenditure(ExpenditureCreateRequest request, Member member) {
        Category category = categoryService.findByCategoryType(request.getCategory());
        Expenditure expenditure = request.toEntity(member, category);

        return expenditureRepository.save(expenditure)
                                    .getId();
    }

    @Transactional(readOnly = true)
    public ExpenditureListResponse retrieveExpenditureList(ExpenditureListRetrieveRequest request, Member member) {
        List<Expenditure> expenditureList = expenditureRepository.retrieveExpenditureList(
                member, request.getCategory(), request.getBeginDate(), request.getEndDate(),
                request.getMinAmount(), request.getMaxAmount());

        List<ExpenditureSummaryResponse> expenditureSummaryList = expenditureList.stream()
                                                                                 .map(ExpenditureSummaryResponse::of)
                                                                                 .collect(Collectors.toList());
        // 카테고리 별 지출 합계
        Map<CategoryType, Long> totalExpenditureByCategory =
                expenditureList.stream()
                               .filter(expenditure -> !expenditure.getExcludeFromTotal())
                               .collect(Collectors.groupingBy(Expenditure::getCategoryType,
                                       Collectors.summingLong(Expenditure::getAmount)));

        // 지출 합계
        Long totalAmount = totalExpenditureByCategory.values().stream()
                                                     .mapToLong(Long::longValue)
                                                     .sum();

        return ExpenditureListResponse.of(totalAmount, totalExpenditureByCategory, expenditureSummaryList);
    }

    @Transactional
    public Boolean updateExpenditure(ExpenditureUpdateRequest request, Member member, Long expenditureId) {
        Category category = categoryService.findByCategoryType(request.getCategory());
        Expenditure expenditure = expenditureRepository.findById(expenditureId)
                                                       .orElseThrow(() -> new DailyBudgetAppException(EXPENDITURE_NOT_FOUND));
        if (!expenditure.isOwner(member)) {
            throw new DailyBudgetAppException(EXPENDITURE_MEMBER_MISMATCH);
        }

        return expenditure.update(request.toEntity(category));
    }

    @Transactional
    public void deleteExpenditure(Member member, Long expenditureId) {
        Expenditure expenditure = expenditureRepository.findById(expenditureId)
                                                       .orElseThrow(() -> new DailyBudgetAppException(EXPENDITURE_NOT_FOUND));

        if (!expenditure.isOwner(member)) {
            throw new DailyBudgetAppException(EXPENDITURE_MEMBER_MISMATCH);
        }

        expenditureRepository.delete(expenditure);
    }

}
