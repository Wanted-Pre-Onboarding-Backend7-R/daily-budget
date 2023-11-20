package com.mojh.daliybudget.member.dto;

import com.mojh.daliybudget.common.vaildation.ValidEnum;
import com.mojh.daliybudget.member.domain.Member;
import com.mojh.daliybudget.member.domain.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignupRequest {

    @NotBlank
    @Size(min = 4, max = 20)
    private String accountId;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @ValidEnum(enumClass = Role.class)
    private Role role;

    @NotBlank
    @Size(min = 1, max = 20)
    private String name;

    private Boolean allowDailyBudgetNotification = false;

    private Boolean allowDailyExpenditureNotification = false;

    public MemberSignupRequest(String accountId, String password, Role role, String name,
                               Boolean allowDailyBudgetNotification, Boolean allowDailyExpenditureNotification) {
        this.accountId = accountId;
        this.password = password;
        this.role = role;
        this.name = name;
        this.allowDailyBudgetNotification = allowDailyBudgetNotification;
        this.allowDailyExpenditureNotification = allowDailyExpenditureNotification;
    }

    public Member toEntity(String encodedPassword) {
        return Member.builder()
                     .accountId(accountId)
                     .password(encodedPassword)
                     .role(role)
                     .name(name)
                     .allowDailyBudgetNotification(allowDailyBudgetNotification)
                     .allowDailyExpenditureNotification(allowDailyExpenditureNotification)
                     .build();
    }

}