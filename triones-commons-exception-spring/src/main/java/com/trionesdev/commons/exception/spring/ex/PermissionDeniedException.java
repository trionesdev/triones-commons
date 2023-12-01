package com.trionesdev.commons.exception.spring.ex;

import com.trionesdev.commons.exception.TrionesError;

@Deprecated
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

    public PermissionDeniedException(TrionesError error) {
        super(error);
    }
}
