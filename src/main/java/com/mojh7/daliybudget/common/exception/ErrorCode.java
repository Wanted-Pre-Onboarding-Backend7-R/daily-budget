package com.mojh7.daliybudget.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COM0000", "시스템 오류"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COM0001", "잘못된 파라미터"),

    // member

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
