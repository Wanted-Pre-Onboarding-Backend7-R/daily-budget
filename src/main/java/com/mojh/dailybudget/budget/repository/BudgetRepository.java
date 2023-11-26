package com.mojh.dailybudget.budget.repository;

import com.mojh.dailybudget.budget.domain.Budget;
import com.mojh.dailybudget.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByMemberAndBudgetYearAndBudgetMonth(Member member, Integer budgetYear, Integer budgetMonth);

}
