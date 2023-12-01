package com.trionesdev.commons.exception;

public class ValidationException extends TrionesException{
    public ValidationException() {
        super(TrionesError.builder().message("validation fail").build());
    }

    public ValidationException(String code, Object... params) {
        super(code, params);
    }

    public ValidationException(Throwable cause, String code, Object... params) {
        super(cause, code, params);
    }

    public ValidationException(TrionesError error) {
        super(error);
    }
}
