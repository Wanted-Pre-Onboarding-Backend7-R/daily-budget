package com.mojh.daliybudget.auth.service;

import com.mojh.daliybudget.common.exception.DailyBudgetAppException;
import com.mojh.daliybudget.member.domain.Member;
import com.mojh.daliybudget.member.repository.MemberRepository;
import com.mojh.daliybudget.auth.domain.MemberAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.mojh.daliybudget.common.exception.ErrorCode.MEMBER_NOT_FOUND;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    public UserDetailsServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        Member member = memberRepository.findByAccountId(accountId)
                                        .orElseThrow(() -> new DailyBudgetAppException(MEMBER_NOT_FOUND));
        return new MemberAdapter(member);
    }

}
