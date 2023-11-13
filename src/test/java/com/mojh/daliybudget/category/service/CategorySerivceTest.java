package com.mojh.daliybudget.category.service;

import com.mojh.daliybudget.category.domain.Category;
import com.mojh.daliybudget.category.domain.CategoryType;
import com.mojh.daliybudget.category.repository.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
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
        List<String> result = categorySerivce.retrieveCategoryList();

        // then
        assertThat(result).containsExactlyInAnyOrderElementsOf(expected);
    }

}