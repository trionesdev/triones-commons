package com.trionesdev.commons.exception;

public class DuplicatedException extends TrionesException {
    public DuplicatedException() {
        super("duplicated");
    }

    public DuplicatedException(String code, ArgumentValue... params) {
        super(code, params);
    }

    public DuplicatedException(String code, String defaultMessage, ArgumentValue... params) {
        super(code, defaultMessage, params);
    }

    public DuplicatedException(Throwable cause, String code, ArgumentValue... params) {
        super(cause, code, params);
    }

    public DuplicatedException(TrionesError error) {
        super(error);
    }
}
