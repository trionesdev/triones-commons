package com.trionesdev.commons.exception;

public class PermissionDeniedException extends TrionesException {
    private static final long serialVersionUID = -3770443711124018035L;

    public PermissionDeniedException() {
        super("actor has no permission");
    }

    public PermissionDeniedException(String code, Object... params) {
        super(code, params);
    }

    public PermissionDeniedException(Throwable cause, String code, Object... params) {
        super(cause, code, params);
    }

    public PermissionDeniedException(TrionesError error) {
        super(error);
    }
}
