package com.moensun.commons.exception;

public class BusinessException extends TrionesException{
    public BusinessException() {
        super(TrionesError.builder().message("business exception").build());
    }
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String code, Object... params) {
        super(code, params);
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public BusinessException(Throwable cause, String code, String message) {
        super(cause, code, message);
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
        super(message, cause, enableSuppression, writableStackTrace, code);
    }

    public BusinessException(TrionesError error) {
        super(error);
    }
}
