package com.moensun.commons.exception.spring.ex;

public class BusinessException extends ResourceException{
    public BusinessException(String code, Object... params) {
        super(code, params);
    }

    public BusinessException(Throwable cause, String code, Object... params) {
        super(cause, code, params);
    }
}
