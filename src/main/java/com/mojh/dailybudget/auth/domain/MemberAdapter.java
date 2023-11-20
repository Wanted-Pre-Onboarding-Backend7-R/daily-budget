package com.mojh.dailybudget.auth.domain;

import com.mojh.dailybudget.member.domain.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class MemberAdapter extends User {

    private Member member;

    public MemberAdapter(Member member) {
        super(member.getAccountId(), "", List.of(new SimpleGrantedAuthority(member.getRole().toString())));
        this.member = member;
    }

}
