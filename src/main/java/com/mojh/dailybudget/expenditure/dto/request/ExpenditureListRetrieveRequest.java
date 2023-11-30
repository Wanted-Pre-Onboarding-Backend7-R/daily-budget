package com.mojh.dailybudget.expenditure.dto.request;

import com.mojh.dailybudget.common.dto.request.AmountRangeRequest;
import com.mojh.dailybudget.common.dto.request.PeriodRequest;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Getter
public class ExpenditureListRetrieveRequest {

    @Valid
    private PeriodRequest period;

    @Valid
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

    public LocalDateTime getBeginDate() {
        return period.getBeginDate();
    }

    public LocalDateTime getEndDate() {
        return period.getEndDate();
    }

    public Long getMinAmount() {
        return amountRangeRequest.getMinAmount();
    }

    public Long getMaxAmount() {
        return amountRangeRequest.getMaxAmount();
    }

}
