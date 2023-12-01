package com.mojh.dailybudget.expenditure.dto.request;

import com.mojh.dailybudget.expenditure.validation.ValidExpenditureListRetrieveRequest;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@ValidExpenditureListRetrieveRequest
public class ExpenditureListRetrieveRequest {

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime beginDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    @Range(min = 0L, max = 1000000000000L)
    private Long minAmount;

    @Range(min = 0L, max = 1000000000000L)
    private Long maxAmount;

    private String category;

    public ExpenditureListRetrieveRequest(LocalDateTime beginDate, LocalDateTime endDate,
                                          Long minAmount, Long maxAmount, String category) {
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.minAmount = (minAmount != null) ? minAmount : 0L;
        this.maxAmount = (maxAmount != null) ? maxAmount : 1000000000000L;
        this.category = category;
    }

}
