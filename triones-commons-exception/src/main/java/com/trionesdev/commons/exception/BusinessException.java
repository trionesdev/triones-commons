package com.trionesdev.commons.exception;

public class BusinessException extends TrionesException{
    public BusinessException() {
        super(TrionesError.builder().message("business exception").build());
    }

    public BusinessException(String code, Argument... params) {
        super(code, params);
    }

    public BusinessException(String code, String defaultMessage, Argument... params) {
        super(code, defaultMessage, params);
    }

    public BusinessException(Throwable cause, String code, Argument... params) {
        super(cause, code, params);
    }

    public BusinessException(TrionesError error) {
        super(error);
    }
}
