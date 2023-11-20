package com.mojh.daliybudget.auth.domain;

import com.mojh.daliybudget.member.domain.Member;
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
