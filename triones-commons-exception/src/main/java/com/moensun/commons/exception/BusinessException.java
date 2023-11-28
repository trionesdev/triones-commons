package com.moensun.commons.exception;

public class BusinessException extends TrionesException{
    public BusinessException() {
        super(TrionesError.builder().message("business exception").build());
    }

    public BusinessException(String code, Object... params) {
        super(code, params);
    }

    public BusinessException(Throwable cause, String code, Object... params) {
        super(cause, code, params);
    }

    public BusinessException(TrionesError error) {
        super(error);
    }
}
