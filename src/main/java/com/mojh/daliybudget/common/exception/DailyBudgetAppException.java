package com.mojh.daliybudget.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DailyBudgetAppException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMessage;

    public DailyBudgetAppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        errorMessage = errorCode.getMessage();
        this.errorCode = errorCode;
    }

    public DailyBudgetAppException(ErrorCode errorCode, String detail) {
        super(message(errorCode, detail));
        errorMessage = message(errorCode, detail);
        this.errorCode = errorCode;
    }

    public DailyBudgetAppException(Throwable cause) {
        super(cause);
        errorMessage = errorCode.getMessage();
    }

    public DailyBudgetAppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        errorMessage = errorCode.getMessage();
    }

    public DailyBudgetAppException(ErrorCode errorCode, Throwable cause, String detail) {
        super(message(errorCode, detail), cause);
        errorMessage = message(errorCode, detail);
    }

    private static String message(ErrorCode errorCode, String detail) {
        if(detail == null) {
            return errorCode.getMessage();
        }

        return errorCode.getMessage() + detail;
    }
}
