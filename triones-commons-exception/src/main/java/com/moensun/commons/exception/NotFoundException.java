package com.moensun.commons.exception;

public class NotFoundException extends TrionesException {
    private static final long serialVersionUID = -3770443711124018035L;

    public NotFoundException() {
        super("not found");
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(String code, String message) {
        super(code, message);
    }

    public NotFoundException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public NotFoundException(Throwable cause, String code, String message) {
        super(cause, code, message);
    }

    public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
        super(message, cause, enableSuppression, writableStackTrace, code);
    }

    public NotFoundException(TrionesError error) {
        super(error);
    }
}
