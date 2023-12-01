package com.mojh.dailybudget.member;

import com.mojh.dailybudget.member.domain.Member;
import com.mojh.dailybudget.member.domain.Role;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static Member MEMBER3() {
        return Member.builder()
                     .accountId("member3")
                     .password("$2a$10$HKlWvxQ0Ce/w3gBRjbHEiOlLT7AgzoV60OH.8J3gJUhq1NhtdKRri")
                     .role(Role.ROLE_USER)
                     .name("이개발")
                     .allowDailyBudgetNotification(false)
                     .allowDailyExpenditureNotification(false)
                     .build();
    }

    public static Member MEMBER4() {
        return Member.builder()
                     .accountId("member4")
                     .password("$2a$10$zeI4mAQy2Xn4Z8rP/cxqguSAB/t7.5I9oTKdqc2BU1BrbzJ/7JhuS")
                     .role(Role.ROLE_USER)
                     .name("박명수")
                     .allowDailyBudgetNotification(false)
                     .allowDailyExpenditureNotification(false)
                     .build();
    }

    public static Member MEMBER5() {
        return Member.builder()
                     .accountId("member5")
                     .password("$2a$10$hIJnOOCZ6HjpLOTXaE3b2eZhmFGTkaFERctDrHpT4rRksrtsSyuiG")
                     .role(Role.ROLE_USER)
                     .name("정형돈")
                     .allowDailyBudgetNotification(false)
                     .allowDailyExpenditureNotification(false)
                     .build();
    }

}
