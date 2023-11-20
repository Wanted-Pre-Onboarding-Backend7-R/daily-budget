package com.mojh.dailybudget.category.service;

import com.mojh.dailybudget.category.repository.CategoryRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;

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

}

