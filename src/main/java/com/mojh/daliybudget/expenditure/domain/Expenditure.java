package com.mojh.daliybudget.expenditure.domain;

import com.mojh.daliybudget.category.domain.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false, columnDefinition = "char", length = 40)
    private String memo;

    @Column(nullable = false)
    private Boolean excludeFromTotal;

    @Column(nullable = false)
    private LocalDateTime expenditureAt;

    @Builder
    public Expenditure(Category category, Long amount, String memo,
                       Boolean excludeFromTotal, LocalDateTime expenditureAt) {
        this.category = category;
        this.amount = amount;
        this.memo = memo;
        this.excludeFromTotal = excludeFromTotal;
        this.expenditureAt = expenditureAt;
    }

}
