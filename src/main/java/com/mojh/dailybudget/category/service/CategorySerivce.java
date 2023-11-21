package com.mojh.dailybudget.category.service;

import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.category.repository.CategoryRepository;
import com.mojh.dailybudget.common.exception.DailyBudgetAppException;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mojh.dailybudget.common.exception.ErrorCode.CATEGORY_NOT_FOUND;

@Service
public class CategorySerivce {

    private final CategoryRepository categoryRepository;

    public CategorySerivce(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<String> retrieveCategoryList() {
        return Streamable.of(categoryRepository.findAll())
                         .map(c -> c.getType().name())
                         .toList();
    }

    public Category findByCategoryType(CategoryType categoryType) {
        return categoryRepository.findByType(categoryType)
                                 .orElseThrow(() -> new DailyBudgetAppException(CATEGORY_NOT_FOUND));
    }

}

