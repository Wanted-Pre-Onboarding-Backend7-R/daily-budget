package com.mojh.dailybudget.expenditure.dto.request;

import com.mojh.dailybudget.common.dto.request.AmountRangeRequest;
import com.mojh.dailybudget.common.dto.request.PeriodRequest;
import com.mojh.dailybudget.common.vaildation.annotation.ValidAmountRangeRequest;
import com.mojh.dailybudget.common.vaildation.annotation.ValidPeriodRequest;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Getter
public class ExpenditureListRetrieveRequest {

    @Valid
    @ValidPeriodRequest
    private PeriodRequest period;

    @Valid
    @ValidAmountRangeRequest
    private AmountRangeRequest amountRangeRequest;

    private String category;

    public ExpenditureListRetrieveRequest(@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime beginDate,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate,
                                          Long minAmount, Long maxAmount, String category) {
        this.period = PeriodRequest.builder()
                                   .beginDate(beginDate)
                                   .endDate(endDate)
                                   .build();
        this.amountRangeRequest = AmountRangeRequest.builder()
                                                    .minAmount(minAmount)
                                                    .maxAmount(maxAmount)
                                                    .build();
        this.category = category;
    }

}
