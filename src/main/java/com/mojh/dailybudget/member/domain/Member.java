package com.mojh.dailybudget.member.domain;

import com.mojh.dailybudget.common.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "accountId", callSuper = false)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String accountId;

    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(name = "allow_daily_budget_noti", nullable = false)
    private Boolean allowDailyBudgetNotification;

    @Column(name = "allow_daily_expenditure_noti", nullable = false)
    private Boolean allowDailyExpenditureNotification;

    @Builder
    public Member(String accountId, String password, Role role, String name,
                  Boolean allowDailyBudgetNotification, Boolean allowDailyExpenditureNotification) {
        this.accountId = accountId;
        this.password = password;
        this.role = role;
        this.name = name;
        this.allowDailyBudgetNotification = allowDailyBudgetNotification;
        this.allowDailyExpenditureNotification = allowDailyExpenditureNotification;
    }

}
