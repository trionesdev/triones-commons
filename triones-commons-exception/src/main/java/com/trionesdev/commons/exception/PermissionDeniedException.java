package com.trionesdev.commons.exception;

public class PermissionDeniedException extends TrionesException {
    private static final long serialVersionUID = -3770443711124018035L;

    public PermissionDeniedException() {
        super("actor has no permission");
    }

    public PermissionDeniedException(String code, Argument... params) {
        super(code, params);
    }

    public PermissionDeniedException(String code, String defaultMessage, Argument... params) {
        super(code, defaultMessage, params);
    }

    public PermissionDeniedException(Throwable cause, String code, Argument... params) {
        super(cause, code, params);
    }

    public PermissionDeniedException(TrionesError error) {
        super(error);
    }
}
