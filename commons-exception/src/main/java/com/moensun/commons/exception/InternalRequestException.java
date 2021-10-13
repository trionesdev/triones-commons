package com.moensun.commons.exception;

public class InternalRequestException extends MSException{
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

    public InternalRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String message1) {
        super(message, cause, enableSuppression, writableStackTrace, code, message1);
    }

    public InternalRequestException(MSError error) {
        super(error);
    }
}
