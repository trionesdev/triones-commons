package com.moensun.commons.exception.spring.ex;

public class PermissionDeniedException extends ResourceException{
    public PermissionDeniedException(String code, Object... params) {
        super(code, params);
    }

    public PermissionDeniedException(Throwable cause, String code, Object... params) {
        super(cause, code, params);
    }
}
