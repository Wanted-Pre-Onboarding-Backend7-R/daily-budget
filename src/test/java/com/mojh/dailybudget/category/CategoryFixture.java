package com.mojh.dailybudget.category;


import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryFixture {

    public static Map<CategoryType, Category> CATEGORY = Arrays.stream(CategoryType.values())
                                                               .collect(Collectors.toMap(Function.identity(), Category::new));

}
