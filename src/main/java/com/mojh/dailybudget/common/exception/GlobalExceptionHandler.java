package com.mojh.dailybudget.common.exception;

import com.mojh.dailybudget.common.web.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.mojh.dailybudget.common.exception.ErrorCode.COM_BAD_REQUEST;
import static com.mojh.dailybudget.common.exception.ErrorCode.COM_INVALID_PARAMETERS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DailyBudgetAppException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(DailyBudgetAppException ex) {
        logError(ex);
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                             .body(ApiResponse.error(ex));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResponse<?> handleBindException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
          .getAllErrors()
          .forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));

        logError(ex);
        return ApiResponse.error(COM_INVALID_PARAMETERS, errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupportedException
            (HttpRequestMethodNotSupportedException ex) {
        logError(ex);
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();

        if (supportedMethods == null) {
            return ResponseEntity.status(METHOD_NOT_ALLOWED)
                                 .body(ApiResponse.error(ex.getMessage()));
        }

        return ResponseEntity.status(METHOD_NOT_ALLOWED)
                             .allow(supportedMethods.toArray(HttpMethod[]::new))
                             .body(ApiResponse.error(ex.getMessage()));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageConversionException.class)
    public ApiResponse<?> handleHttpMessageConversionException(HttpMessageConversionException ex) {
        logError(ex);
        return ApiResponse.error(COM_BAD_REQUEST);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception ex) {
        logError(ex, ErrorCode.COM_INTERNAL_SERVER_ERROR.name());
        return ApiResponse.error(ErrorCode.COM_INTERNAL_SERVER_ERROR);
    }

    private void logError(Exception ex) {
        log.error(ex.getClass().getSimpleName(), ex);
    }

    private void logError(Exception ex, String detailMessage) {
        String message = String.format("%s (%s) ", ex.getClass()
                                                     .getSimpleName(), detailMessage);
        log.error(message, ex);
    }
}
