package com.mojh.daliybudget.member.controller;

import com.mojh.daliybudget.common.web.ApiResponse;
import com.mojh.daliybudget.member.dto.MemberSignupRequest;
import com.mojh.daliybudget.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> signup(@RequestBody @Valid final MemberSignupRequest request) {
        memberService.signup(request);
        return ApiResponse.succeed();
    }

}
