package com.moensun.commons.exception;

public class PermissionDeniedException extends TrionesException {
    private static final long serialVersionUID = -3770443711124018035L;

    public PermissionDeniedException() {
        super("actor has no permission");
    }

    public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionDeniedException(String code, String message) {
        super(code, message);
    }

    public PermissionDeniedException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public PermissionDeniedException(Throwable cause, String code, String message) {
        super(cause, code, message);
    }

    public PermissionDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
        super(message, cause, enableSuppression, writableStackTrace, code);
    }

    public PermissionDeniedException(TrionesError error) {
        super(error);
    }
}
