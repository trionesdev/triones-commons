package com.moensun.commons.exception.spring.ex;

public class ValidationException extends ResourceException{
    public ValidationException(String code, Object... params) {
        super(code, params);
    }

    public ValidationException(Throwable cause, String code, Object... params) {
        super(cause, code, params);
    }
}
