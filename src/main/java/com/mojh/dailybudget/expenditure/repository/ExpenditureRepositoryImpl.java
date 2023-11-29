package com.mojh.dailybudget.expenditure.repository;


import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.expenditure.domain.QExpenditure;
import com.mojh.dailybudget.member.domain.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.List;

import static com.mojh.dailybudget.expenditure.domain.QExpenditure.expenditure;


public class ExpenditureRepositoryImpl implements ExpenditureRepositroyCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ExpenditureRepositoryImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Expenditure> retrieveExpenditureList(Member member, String category, LocalDateTime beginDate,
                                                     LocalDateTime endDate, Long minAmount, Long maxAmount) {
        BooleanExpression memberEquals = expenditure.member.eq(member);

        // 기간으로 조회
        BooleanExpression dateBetween = expenditure.expenditureAt.between(beginDate, endDate);

        // category를 선택 했으면 특정 카테고리만 조회
        BooleanExpression categoryEquals = (category == null) ? null
                : expenditure.category.type.eq(CategoryType.valueOf(category));

        // 최소, 최대 금액으로 조회
        BooleanExpression amountBetween = expenditure.amount.between(minAmount, maxAmount);

        return jpaQueryFactory.selectFrom(expenditure)
                              .where(categoryEquals, memberEquals, amountBetween, dateBetween)
                              .orderBy(expenditure.expenditureAt.desc())
                              .fetch();
    }

}
