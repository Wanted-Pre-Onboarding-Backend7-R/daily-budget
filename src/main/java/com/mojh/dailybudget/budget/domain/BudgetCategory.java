package com.mojh.dailybudget.budget.domain;

import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BudgetCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Long amount;

    @Builder
    public BudgetCategory(Budget budget, Category category, Long amount) {
        this.budget = budget;
        this.category = category;
        this.amount = amount;
    }

    public CategoryType getCategoryType() {
        return category.getType();
    }

    public void updateAmount(Long amount) {
        // TODO: dto에서 amount validation 처리 하고 있지만 entity에도 추가 해야 될 수도, 추가하면 최대 금액 어디서 관리?
        this.amount = amount;
    }

    public static BudgetCategory of(CategoryType categoryType, Long amount) {
        Category category = new Category(categoryType);
        return BudgetCategory.builder()
                             .category(category)
                             .amount(amount)
                             .build();
    }

}