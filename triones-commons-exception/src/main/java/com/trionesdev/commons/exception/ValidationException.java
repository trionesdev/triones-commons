package com.trionesdev.commons.exception;

public class ValidationException extends TrionesException{
    public ValidationException() {
        super(TrionesError.builder().message("validation fail").build());
    }

    public ValidationException(String code, Argument... params) {
        super(code, params);
    }

    public ValidationException(String code, String defaultMessage, Argument... params) {
        super(code, defaultMessage, params);
    }

    public ValidationException(Throwable cause, String code, Argument... params) {
        super(cause, code, params);
    }

    public ValidationException(TrionesError error) {
        super(error);
    }
}
