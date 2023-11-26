package com.moensun.commons.exception.spring.ex;

import com.moensun.commons.exception.TrionesError;

public class NotFoundException extends ResourceException{

    public NotFoundException(){
        super("not found exception");
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