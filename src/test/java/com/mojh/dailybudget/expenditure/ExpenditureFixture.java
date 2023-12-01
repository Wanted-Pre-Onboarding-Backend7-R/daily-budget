package com.mojh.dailybudget.expenditure;

import com.mojh.dailybudget.category.CategoryFixture;
import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.common.tmp.TestUilts;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.member.MemberFixture;
import com.mojh.dailybudget.member.domain.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mojh.dailybudget.category.domain.CategoryType.EDUCATION;
import static com.mojh.dailybudget.category.domain.CategoryType.FOOD;
import static com.mojh.dailybudget.category.domain.CategoryType.SHOPPING;
import static com.mojh.dailybudget.category.domain.CategoryType.UNCATEGORIZED;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExpenditureFixture {

    public static List<Expenditure> EXPENDITURE_LIST() {
        Member member1 = MemberFixture.MEMBER1();
        Member member2 = MemberFixture.MEMBER2();
        Member member3 = MemberFixture.MEMBER3();
        Map<CategoryType, Category> categoryMap = CategoryFixture.CATEGORY;
        List<Expenditure> expenditureListFixture = new ArrayList<>();

        Expenditure expenditure;

        for (int idx = 1; idx <= 5; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 02, 10 + idx, 30);
            expenditure = Expenditure.builder()
                                     .member(member1)
                                     .category(categoryMap.get(FOOD))
                                     .amount(10000L * idx)
                                     .memo("음식 " + idx)
                                     .excludeFromTotal(false)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureListFixture.add(expenditure);
        }

        for (int idx = 6; idx <= 7; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 03, 10 + idx, 30);
            expenditure = Expenditure.builder()
                                     .member(member1)
                                     .category(categoryMap.get(EDUCATION))
                                     .amount(20000L * idx)
                                     .memo("강의 구매 " + idx)
                                     .excludeFromTotal(false)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureListFixture.add(expenditure);
        }

        for (int idx = 8; idx <= 9; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 06, 10 + idx, 30);
            expenditure = Expenditure.builder()
                                     .member(member2)
                                     .category(categoryMap.get(UNCATEGORIZED))
                                     .amount(10000L * idx)
                                     .memo("기타 " + idx)
                                     .excludeFromTotal(false)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureListFixture.add(expenditure);
        }

        for (int idx = 10; idx <= 11; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 07, 10 + idx, 30);
            expenditure = Expenditure.builder()
                                     .member(member2)
                                     .category(categoryMap.get(UNCATEGORIZED))
                                     .amount(10000L * idx)
                                     .memo("기타 " + idx)
                                     .excludeFromTotal(true)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureListFixture.add(expenditure);
        }

        for (int idx = 12; idx <= 15; idx++) {
            LocalDateTime date = LocalDateTime.of(2023, 11, 9, 10, idx);
            expenditure = Expenditure.builder()
                                     .member(member2)
                                     .category(categoryMap.get(SHOPPING))
                                     .amount(1000L * idx)
                                     .memo("쇼핑 " + idx)
                                     .excludeFromTotal(false)
                                     .expenditureAt(date)
                                     .build();
            TestUilts.setId(expenditure, idx);
            expenditureListFixture.add(expenditure);
        }

        return expenditureListFixture;
    }

}