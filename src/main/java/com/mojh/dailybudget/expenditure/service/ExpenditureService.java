package com.mojh.dailybudget.expenditure.service;

import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.service.CategorySerivce;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.expenditure.dto.ExpenditureCreateRequest;
import com.mojh.dailybudget.expenditure.repository.ExpenditureRepository;
import com.mojh.dailybudget.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
