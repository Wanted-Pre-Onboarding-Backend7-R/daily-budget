package com.mojh.dailybudget.member.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum Role {
    ROLE_USER;

    @JsonCreator
    public static Role parse(String input) {
        String inputUpperCase = input.toUpperCase();
        return Stream.of(Role.values())
                     .filter(role -> role.toString().equals(inputUpperCase))
                     .findFirst()
                     .orElse(null);
    }

}
