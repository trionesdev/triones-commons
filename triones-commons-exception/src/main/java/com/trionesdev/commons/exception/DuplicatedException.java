package com.trionesdev.commons.exception;

public class DuplicatedException extends TrionesException {
    public DuplicatedException() {
        super("duplicated");
    }


    public DuplicatedException(String code, Object... params) {
        super(code, params);
    }

    public DuplicatedException(Throwable cause, String code, Object... params) {
        super(cause, code, params);
    }


    public DuplicatedException(TrionesError error) {
        super(error);
    }
}
