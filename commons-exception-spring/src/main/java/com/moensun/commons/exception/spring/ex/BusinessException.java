package com.moensun.commons.exception.spring.ex;

import com.moensun.commons.exception.MSError;

public class BusinessException extends ResourceException{

    public BusinessException(){
        super("business exception");
    }

    public BusinessException(String code, Object... params) {
        super(code, params);
    }

    public BusinessException(Throwable cause, String code, Object... params) {
        super(cause, code, params);
    }

    public BusinessException(MSError error) {
        super(error);
    }
}
