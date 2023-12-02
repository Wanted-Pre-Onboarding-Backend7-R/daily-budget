package com.mojh.dailybudget.expenditure.dto.response;

import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExpenditureResponse {

    private CategoryType category;

    private Long amount;

    private String memo;

    private Boolean excludeFromTotal;

    private LocalDateTime expenditureAt;

    @Builder
    public ExpenditureResponse(CategoryType category, Long amount, String memo,
                               Boolean excludeFromTotal, LocalDateTime expenditureAt) {
        this.category = category;
        this.amount = amount;
        this.memo = memo;
        this.excludeFromTotal = excludeFromTotal;
        this.expenditureAt = expenditureAt;
    }

    public static ExpenditureResponse of(Expenditure expenditure) {
        return ExpenditureResponse.builder()
                                  .category(expenditure.getCategoryType())
                                  .amount(expenditure.getAmount())
                                  .memo(expenditure.getMemo())
                                  .excludeFromTotal(expenditure.getExcludeFromTotal())
                                  .expenditureAt(expenditure.getExpenditureAt())
                                  .build();
    }

}
