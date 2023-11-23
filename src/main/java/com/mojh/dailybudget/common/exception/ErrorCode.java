package com.mojh.dailybudget.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

// TODO: domain 많아지면 domain별로 관리해도 좋을 듯

@Getter
public enum ErrorCode {

    // common
    COM_INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, "COM0000", "시스템 오류입니다."),
    COM_BAD_REQUEST(BAD_REQUEST, "COM0001", "잘못된 요청입니다."),
    COM_INVALID_PARAMETERS(BAD_REQUEST, "COM0002", "유효하지 않은 파라미터입니다."),
    COM_PATCH_FAILED(BAD_REQUEST, "COM0003", "데이터 수정에 실패했습니다."),

    // member
    MEMBER_NOT_FOUND(NOT_FOUND, "MEM0001", "유저를 찾을 수 없습니다."),
    DUPLICATE_ACCOUNT_ID(BAD_REQUEST, "MEM0002", "중복된 아이디입니다."),

    // auth
    INVALID_TOKEN(UNAUTHORIZED, "AUTH0001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "AUTH0002", "만료된 토큰입니다."),
    MISMATCHED_TOKENS_ACCOUNT(UNAUTHORIZED, "AUTH0003", "액세스와 리프레시 토큰의 계정 정보가 일치하지 않습니다"),
    ALREADY_LOGGED_OUT(UNAUTHORIZED, "AUTH0004", "이미 로그아웃 처리된 유저입니다."),
    LOGIN_FAILED(BAD_REQUEST, "AUTH0005", "아이디 혹은 비밀번호가 일치하지 않습니다."),


    // budget
    TOTAL_BUDGET_LIMIT_EXCESS(BAD_REQUEST, "BUD0001", "총 예산은 1조원을 초과하여 설정할 수 없습니다."),

    // expenditure
    EXPENDITURE_NOT_FOUND(NOT_FOUND, "EXP0001", "지출 정보를 찾을 수 없습니다."),
    EXPENDITURE_MEMBER_MISMATCH(BAD_REQUEST, "EXP0002", "해당 지출 정보를 작성한 유저와 다릅니다."),

    // category
    CATEGORY_NOT_FOUND(NOT_FOUND, "CATE0001", "카테고리를 찾을 수 없습니다.")

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
