package com.trionesdev.commons.exception;

public class NotFoundException extends TrionesException {
    private static final long serialVersionUID = -3770443711124018035L;

    public NotFoundException() {
        super("not found");
    }

    public NotFoundException(String code, ArgumentValue... params) {
        super(code, params);
    }

    public NotFoundException(String code, String defaultMessage, ArgumentValue... params) {
        super(code, defaultMessage, params);
    }

    public NotFoundException(Throwable cause, String code, ArgumentValue... params) {
        super(cause, code, params);
    }

    public NotFoundException(TrionesError error) {
        super(error);
    }
}
