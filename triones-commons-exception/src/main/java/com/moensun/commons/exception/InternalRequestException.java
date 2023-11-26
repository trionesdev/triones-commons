package com.moensun.commons.exception;

public class InternalRequestException extends TrionesException {
    private static final long serialVersionUID = 3851795892433283556L;

    public InternalRequestException() {
    }

    public InternalRequestException(String code, Object... params) {
        super(code, params);
    }

    public InternalRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalRequestException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public InternalRequestException(Throwable cause, String code, String message) {
        super(cause, code, message);
    }

    public InternalRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
        super(message, cause, enableSuppression, writableStackTrace,code);
    }

    public InternalRequestException(TrionesError error) {
        super(error);
    }
}
