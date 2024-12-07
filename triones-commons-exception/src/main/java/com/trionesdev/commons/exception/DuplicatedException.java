package com.trionesdev.commons.exception;

public class DuplicatedException extends TrionesException {
    public DuplicatedException() {
        super("duplicated");
    }

    public DuplicatedException(String code, Argument... params) {
        super(code, params);
    }

    public DuplicatedException(String code, String defaultMessage, Argument... params) {
        super(code, defaultMessage, params);
    }

    public DuplicatedException(Throwable cause, String code, Argument... params) {
        super(cause, code, params);
    }

    public DuplicatedException(TrionesError error) {
        super(error);
    }
}
