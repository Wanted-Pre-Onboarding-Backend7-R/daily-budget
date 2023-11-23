package com.mojh.dailybudget.member.fixture;

import com.mojh.dailybudget.member.domain.Member;
import com.mojh.dailybudget.member.domain.Role;

public final class MemberFixture {

    public static Member MEMBER1() {
        return Member.builder()
                     .accountId("member1")
                     .password("$2a$10$x6a5r29ElOc5nThogQozT.ZZijwjZKEODkl52ayATgVtMm5gbe4Zy")
                     .role(Role.ROLE_USER)
                     .name("홍길동")
                     .allowDailyBudgetNotification(false)
                     .allowDailyExpenditureNotification(false)
                     .build();
    }

    public static Member MEMBER2() {
        return Member.builder()
                     .accountId("member2")
                     .password("$2a$10$559fNXUZW3MFH2IUEACAn.Xm.LXnPHs25rySDLbj1oxKQ2VAavIEu")
                     .role(Role.ROLE_USER)
                     .name("김철수")
                     .allowDailyBudgetNotification(false)
                     .allowDailyExpenditureNotification(false)
                     .build();
    }

}
