package com.moensun.commons.exception.spring.ex;

import com.moensun.commons.core.spring.context.SpringContextHolder;
import com.moensun.commons.exception.MSException;
import com.moensun.commons.exception.spring.resource.ExceptionMessageHandler;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

public abstract class ResourceException extends MSException {
    private String code;
    private String message;
    private transient ExceptionMessageHandler exceptionMessageHandler;

    @Override public String getCode() {
        return code;
    }

    @Override public String getMessage() {
        return message;
    }

    public ExceptionMessageHandler getExceptionMessageHandler() {
        return exceptionMessageHandler;
    }

    public ResourceException(String code, Object... params) {
        this( null, code, params);
    }

    public ResourceException( Throwable cause, String code, Object... params) {
        super(code, cause);
        ApplicationContext applicationContext = SpringContextHolder.getApplicationContext();
        if (Objects.nonNull(applicationContext)) {
            this.exceptionMessageHandler = SpringContextHolder.getApplicationContext().getBean(ExceptionMessageHandler.class);
            this.message = exceptionMessageHandler.text(code, params);
            if (Objects.isNull(this.message)) {
                this.message = code;
            } else {
                this.code = code;
            }
        } else {

            this.message = code;
        }
    }
}
