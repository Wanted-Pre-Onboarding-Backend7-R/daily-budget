package com.mojh.dailybudget.budget.repository;

import com.mojh.dailybudget.budget.domain.BudgetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Long> {

}

