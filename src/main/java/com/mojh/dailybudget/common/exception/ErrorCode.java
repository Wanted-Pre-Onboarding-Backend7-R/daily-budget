package com.mojh.dailybudget.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
public enum ErrorCode {

    // common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COM0000", "시스템 오류"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COM0001", "잘못된 파라미터"),

    // member
    MEMBER_NOT_FOUND(NOT_FOUND, "MEM0001", "유저를 찾을 수 없습니다."),
    DUPLICATE_ACCOUNT_ID(HttpStatus.BAD_REQUEST, "MEM0002", "중복된 아이디입니다."),

    // auth
    INVALID_TOKEN(UNAUTHORIZED, "AUTH0001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "AUTH0002", "만료된 토큰입니다."),
    MISMATCHED_TOKENS_ACCOUNT(UNAUTHORIZED, "AUTH0003", "액세스와 리프레시 토큰의 계정 정보가 일치하지 않습니다"),
    ALREADY_LOGGED_OUT(UNAUTHORIZED, "AUTH0004", "이미 로그아웃 처리된 유저입니다."),
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "AUTH0005", "아이디 혹은 비밀번호가 일치하지 않습니다.")


    // budget

    // expenditure

    ;

    private final HttpStatus status;
    private final String code; // 클라이언트 구분용 code
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
