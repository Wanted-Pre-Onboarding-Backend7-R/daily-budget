package com.mojh.dailybudget.category.repository;

import com.mojh.dailybudget.category.domain.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {

}
