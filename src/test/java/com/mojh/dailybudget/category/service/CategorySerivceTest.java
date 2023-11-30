package com.mojh.dailybudget.category.service;

import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.category.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategorySerivceTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategorySerivce categorySerivce;

    @Test
    @DisplayName("카테고리 목록 조회")
    void retrieveCategoryList() {
        // given
        List<Category> categoryList = new ArrayList<>();
        for (CategoryType type : CategoryType.values()) {
            categoryList.add(new Category(type));
        }
        given(categoryRepository.findAll()).willReturn(categoryList);
        List<String> expected = categoryList.stream()
                                            .map(c -> c.getType().name())
                                            .collect(Collectors.toList());

        // when
        List<String> actual = categorySerivce.retrieveCategoryList();

        // then
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

}