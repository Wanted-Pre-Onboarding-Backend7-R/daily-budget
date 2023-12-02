package com.mojh.dailybudget.expenditure.dto.request;

import com.mojh.dailybudget.category.domain.Category;
import com.mojh.dailybudget.category.domain.CategoryType;
import com.mojh.dailybudget.common.vaildation.ValidEnum;
import com.mojh.dailybudget.expenditure.domain.Expenditure;
import com.mojh.dailybudget.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenditureCreateRequest {

    @ValidEnum(enumClass = CategoryType.class)
    private String category;

    @NotNull
    @Range(min = 0L, max = 1000000000000L)
    private Long amount;

    @Size(max = 40)
    private String memo = "";

    private Boolean excludeFromTotal = false;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expenditureAt;

    @Builder
    public ExpenditureCreateRequest(String category, Long amount, String memo,
                                    Boolean excludeFromTotal, LocalDateTime expenditureAt) {
        this.category = category;
        this.amount = amount;
        this.memo = memo;
        this.excludeFromTotal = excludeFromTotal;
        this.expenditureAt = expenditureAt;
    }

    public Expenditure toEntity(Member member, Category category) {
        return Expenditure.builder()
                          .member(member)
                          .category(category)
                          .amount(amount)
                          .memo(memo)
                          .excludeFromTotal(excludeFromTotal)
                          .expenditureAt(expenditureAt)
                          .build();
    }

}
