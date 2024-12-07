package com.trionesdev.commons.exception;

public class InternalRequestException extends TrionesException {
    private static final long serialVersionUID = 3851795892433283556L;

    public InternalRequestException() {
        super(TrionesError.builder().message("internal request exception").build());
    }

    public InternalRequestException(String code, Argument... params) {
        super(code, params);
    }

    public InternalRequestException(String code, String defaultMessage, Argument... params) {
        super(code, defaultMessage, params);
    }

    public InternalRequestException(Throwable cause, String code, Argument... params) {
        super(cause, code, params);
    }

    public InternalRequestException(TrionesError error) {
        super(error);
    }
}
