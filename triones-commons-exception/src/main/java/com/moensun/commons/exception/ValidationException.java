package com.moensun.commons.exception;

public class ValidationException extends TrionesException{
    public ValidationException() {
        super(TrionesError.builder().message("validation fail").build());
    }

    public ValidationException(String code, Object... params) {
        super(code, params);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public ValidationException(Throwable cause, String code, String message) {
        super(cause, code, message);
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
        super(message, cause, enableSuppression, writableStackTrace, code);
    }

    public ValidationException(TrionesError error) {
        super(error);
    }
}
