package com.mojh.dailybudget.expenditure.dto.response;

import com.mojh.dailybudget.category.domain.CategoryType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ExpenditureListResponse {

    private Long totalAmount;

    private Map<CategoryType, Long> totalExpenditureByCategory;

    private List<ExpenditureSummaryResponse> expenditureList;


    public ExpenditureListResponse(Long totalAmount, Map<CategoryType, Long> totalExpenditureByCategory,
                                   List<ExpenditureSummaryResponse> expenditureList) {
        this.totalAmount = totalAmount;
        this.totalExpenditureByCategory = totalExpenditureByCategory;
        this.expenditureList = expenditureList;
    }

    public static ExpenditureListResponse of(Long totalAmount, Map<CategoryType, Long> totalExpenditureByCategory,
                                             List<ExpenditureSummaryResponse> expenditureSummaryList) {
        return new ExpenditureListResponse(totalAmount, totalExpenditureByCategory, expenditureSummaryList);
    }

}
