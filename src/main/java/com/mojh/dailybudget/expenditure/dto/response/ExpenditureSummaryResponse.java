package com.mojh.dailybudget.expenditure.dto.response;

import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 지출 목록 조회에서 지출 상세 조회 대비
 * 요약된 형태로 지출 정보 반환
 */
@Getter
public class ExpenditureSummaryResponse {

    private CategoryType category;

    private Long amount;

    private LocalDateTime expenditureAt;

    @Builder
    public ExpenditureSummaryResponse(CategoryType category, Long amount, LocalDateTime expenditureAt) {
        this.category = category;
        this.amount = amount;
        this.expenditureAt = expenditureAt;
    }

    public static ExpenditureSummaryResponse of(Expenditure expenditure) {
        return ExpenditureSummaryResponse.builder()
                                         .category(expenditure.getCategoryType())
                                         .amount(expenditure.getAmount())
                                         .expenditureAt(expenditure.getExpenditureAt())
                                         .build();
    }
}
