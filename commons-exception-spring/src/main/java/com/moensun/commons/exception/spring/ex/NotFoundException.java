package com.moensun.commons.exception.spring.ex;

import com.moensun.commons.exception.MSError;

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

    public NotFoundException(MSError error) {
        super(error);
    }
}
