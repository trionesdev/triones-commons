package com.trionesdev.commons.exception.spring.ex;

import com.trionesdev.commons.exception.TrionesError;

@Deprecated
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

    public BusinessException(TrionesError error) {
        super(error);
    }
}
