package com.mojh.dailybudget.common.dto.request;

import com.mojh.dailybudget.common.vaildation.annotation.ValidPeriodRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ValidPeriodRequest
public class PeriodRequest {

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime beginDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;


    @Builder
    public PeriodRequest(LocalDateTime beginDate, LocalDateTime endDate) {
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

}