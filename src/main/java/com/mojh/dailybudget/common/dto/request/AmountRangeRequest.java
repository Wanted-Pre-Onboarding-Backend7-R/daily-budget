package com.mojh.dailybudget.common.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor
public class AmountRangeRequest {

    @Range(min = 0L, max = 1000000000000L)
    private Long minAmount = 0L;

    @Range(min = 0L, max = 1000000000000L)
    private Long maxAmount = 1000000000000L;

    @Builder
    public AmountRangeRequest(Long minAmount, Long maxAmount) {
        this.minAmount = (minAmount != null) ? minAmount : 0L;
        this.maxAmount = (maxAmount != null) ? maxAmount : 1000000000000L;
    }

}
