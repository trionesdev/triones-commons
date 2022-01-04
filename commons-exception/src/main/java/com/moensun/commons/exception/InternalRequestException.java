package com.moensun.commons.exception;

public class InternalRequestException extends MSException{
    private static final long serialVersionUID = 3851795892433283556L;

    public InternalRequestException() {
    }

    public InternalRequestException(String message) {
        super(message);
    }

    public InternalRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalRequestException(String code, String message) {
        super(code, message);
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

    public InternalRequestException(MSError error) {
        super(error);
    }
}
