package com.mojh.dailybudget.expenditure.service;

import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.service.CategorySerivce;
import com.mojh.dailybudget.common.exception.DailyBudgetAppException;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.expenditure.dto.ExpenditureCreateRequest;
import com.mojh.dailybudget.expenditure.dto.ExpenditureUpdateRequest;
import com.mojh.dailybudget.expenditure.repository.ExpenditureRepository;
import com.mojh.dailybudget.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return expenditureRepository.save(expenditure).getId();
    }

    @Transactional
    public Boolean updateExpenditure(ExpenditureUpdateRequest request, Member member, Long expenditureId) {
        Category category = categoryService.findByCategoryType(request.getCategory());
        Expenditure expenditure = expenditureRepository.findById(expenditureId)
                                                       .orElseThrow(() -> new DailyBudgetAppException(EXPENDITURE_NOT_FOUND));
        if(!expenditure.isOwner(member)) {
            throw new DailyBudgetAppException(EXPENDITURE_MEMBER_MISMATCH);
        }

        return expenditure.update(request.toEntity(category));
    }

}
