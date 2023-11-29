package com.mojh.dailybudget.common.tmp;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestUilts {

    public static void setId(Object target, int idx) {
        ReflectionTestUtils.setField(target, "id", (long) idx);
    }

    public static void setId(Object target, Long idx) {
        ReflectionTestUtils.setField(target, "id", idx);
    }

}
