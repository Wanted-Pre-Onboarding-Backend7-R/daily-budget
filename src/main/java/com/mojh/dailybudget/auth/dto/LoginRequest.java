package com.mojh.dailybudget.auth.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {

    @NotBlank
    @Size(min = 4, max = 20)
    private String accountId;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    public LoginRequest(String accountId, String password) {
        this.accountId = accountId;
        this.password = password;
    }

}
