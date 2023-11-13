package com.mojh.daliybudget.category.controller;

import com.mojh.daliybudget.category.service.CategorySerivce;
import com.mojh.daliybudget.common.web.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    private final CategorySerivce categorySerivce;


    public CategoryController(CategorySerivce categorySerivce) {
        this.categorySerivce = categorySerivce;
    }

    @GetMapping("/api/categories")
    public ApiResponse<List<String>> retrieveCategoryList() {
        return ApiResponse.succeed(categorySerivce.retrieveCategoryList());
    }

}

