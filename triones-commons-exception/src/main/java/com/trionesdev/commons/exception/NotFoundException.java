package com.trionesdev.commons.exception;

public class NotFoundException extends TrionesException {
    private static final long serialVersionUID = -3770443711124018035L;

    public NotFoundException() {
        super("not found");
    }


    public NotFoundException(String code, Object... params) {
        super(code, params);
    }

    public NotFoundException(Throwable cause, String code, Object... params) {
        super(cause, code, params);
    }


    public NotFoundException(TrionesError error) {
        super(error);
    }
}
