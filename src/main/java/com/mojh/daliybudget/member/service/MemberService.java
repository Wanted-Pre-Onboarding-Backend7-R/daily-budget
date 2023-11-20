package com.mojh.daliybudget.member.service;

import com.mojh.daliybudget.common.exception.DailyBudgetAppException;
import com.mojh.daliybudget.common.exception.ErrorCode;
import com.mojh.daliybudget.member.dto.MemberSignupRequest;
import com.mojh.daliybudget.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mojh.daliybudget.common.exception.ErrorCode.DUPLICATE_ACCOUNT_ID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signup(MemberSignupRequest request) {
        if (memberRepository.existsByAccountId(request.getAccountId())) {
            throw new DailyBudgetAppException(DUPLICATE_ACCOUNT_ID);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        memberRepository.save(request.toEntity(encodedPassword));
    }

}
