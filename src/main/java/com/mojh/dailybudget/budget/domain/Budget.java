package com.mojh.dailybudget.budget.domain;

import com.mojh.dailybudget.common.domain.BaseTimeEntity;
import com.mojh.dailybudget.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Integer budgetYear;

    @Column(nullable = false)
    private Integer budgetMonth;

    @Column(nullable = false)
    private Long totalAmount;

    @OneToMany(mappedBy = "budget", fetch = FetchType.LAZY)
    private List<BudgetCategory> budgetCategoryList = new ArrayList<>();

    @Builder
    public Budget(Member member, Integer budgetYear, Integer budgetMonth,
                  Long totalAmount, List<BudgetCategory> budgetCategoryList) {
        this.member = member;
        this.budgetYear = budgetYear;
        this.budgetMonth = budgetMonth;
        this.totalAmount = totalAmount;
        this.budgetCategoryList = budgetCategoryList;
    }

}
