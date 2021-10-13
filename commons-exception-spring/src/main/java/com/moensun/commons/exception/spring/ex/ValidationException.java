package com.moensun.commons.exception.spring.ex;

import com.moensun.commons.exception.MSError;

public class ValidationException extends ResourceException{

    public ValidationException(){
        super("validation fail");
    }

    public ValidationException(String code, Object... params) {
        super(code, params);
    }

    public ValidationException(Throwable cause, String code, Object... params) {
        super(cause, code, params);
    }

    public ValidationException(MSError error) {
        super(error);
    }
}
