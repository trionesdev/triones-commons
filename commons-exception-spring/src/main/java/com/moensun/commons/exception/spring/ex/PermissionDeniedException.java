package com.moensun.commons.exception.spring.ex;

import com.moensun.commons.exception.MSError;

public class PermissionDeniedException extends ResourceException{
    public PermissionDeniedException(){
        super("permission denied");
    }

    public PermissionDeniedException(String code, Object... params) {
        super(code, params);
    }

    public PermissionDeniedException(Throwable cause, String code, Object... params) {
        super(cause, code, params);
    }

    public PermissionDeniedException(MSError error) {
        super(error);
    }
}
