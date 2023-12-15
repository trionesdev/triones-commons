package com.trionesdev.commons.exception;

import lombok.Getter;

public class TrionesException extends RuntimeException {
    private static final long serialVersionUID = -5195341167826040681L;
    @Getter
    private String code;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public TrionesException() {
        super("triones exception");
    }

    public TrionesException(String code, Object... params) {
        this(null, code, params);
    }

    public TrionesException(Throwable cause, String code, Object... params) {
        super(code, cause);
        this.code = code;
        this.message = ExceptionResourceProperties.text(code, params);
    }

    public TrionesException(TrionesError error) {
        super(error.getMessage(), error.getCause());
        this.code = error.getCode();
        this.message = error.getMessage();
    }
}
