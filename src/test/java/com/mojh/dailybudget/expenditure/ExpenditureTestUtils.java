package com.mojh.dailybudget.expenditure;


import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.member.domain.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.groups.Tuple;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.tuple;

/**
 * 지출 관련 테스트의 결과를 검증하기 위한 데이터를 만드는 함수들을 가진 util class
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExpenditureTestUtils {

    public static Expenditure captureExpenditure(Expenditure expenditure) {
        return Expenditure.builder()
                          .category(expenditure.getCategory())
                          .amount(expenditure.getAmount())
                          .memo(expenditure.getMemo())
                          .excludeFromTotal(expenditure.getExcludeFromTotal())
                          .expenditureAt(expenditure.getExpenditureAt())
                          .build();
    }


    public static List<Expenditure> filteredExpenditureList(List<Expenditure> expenditureList, Member member,
                                                             LocalDateTime beginDate, LocalDateTime endDate,
                                                             Long minAmount, Long maxAmount, String category) {
        Long capturedMinAmount = (minAmount != null) ? minAmount : 0L;
        Long capturedMaxAmount = (maxAmount != null) ? maxAmount : 1000000000000L;
        return expenditureList.stream()
                              .filter(e -> e.getMember().equals(member))
                              .filter(e -> category == null || e.getCategoryType().toString().equals(category))
                              .filter(e -> !e.getExpenditureAt().isBefore(beginDate) &&
                                           !e.getExpenditureAt().isAfter(endDate))
                              .filter(e -> capturedMinAmount <= e.getAmount() && e.getAmount() <= capturedMaxAmount)
                              .sorted(Comparator.comparing(Expenditure::getExpenditureAt).reversed())
                              .collect(Collectors.toList());
    }

    /**
     * 카테고리 별 지출 합계 계산
     * @return
     */
    public static Map<CategoryType, Long> totalExpenditureByCategory(List<Expenditure> expenditureList) {
        return expenditureList.stream()
                              .filter(expenditure -> !expenditure.getExcludeFromTotal())
                              .collect(Collectors.groupingBy(Expenditure::getCategoryType,
                                      Collectors.summingLong(Expenditure::getAmount)));
    }

    /**
     * 카테고리 별 지출 금액을 합친 총 지출 금액 계산
     * @param totalExpenditureByCategory
     * @return
     */
    public static Long totalExpenditureAmount(Map<CategoryType, Long> totalExpenditureByCategory) {
        return totalExpenditureByCategory.values()
                                         .stream()
                                         .mapToLong(Long::longValue)
                                         .sum();
    }

    /**
     * 지출 목록 데이터 검증을 위해 비교할 필드들만 설정한 지출 목록 예상 tuple list
     * @param expenditureList
     * @return
     */
    public static List<Tuple> expenditureListToExpectedList(List<Expenditure> expenditureList) {
        return expenditureList.stream()
                              .map(expenditure -> tuple(expenditure.getCategoryType(),
                                      expenditure.getAmount(), expenditure.getExpenditureAt()))
                              .collect(Collectors.toList());
    }

}
