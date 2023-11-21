package com.mojh.dailybudget.category.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum CategoryType {
    FOOD, TRANSPORTATION, SHOPPING, MEDICAL, EDUCATION, ENTERTAINMENT, FINANCE, PHONE, HOUSE, UNCATEGORIZED;

    @JsonCreator
    public static CategoryType parse(String input) {
        String inputUpperCase = input.toUpperCase();
        return Stream.of(CategoryType.values())
                     .filter(type -> type.toString().equals(inputUpperCase))
                     .findFirst()
                     .orElse(null);
    }

}
