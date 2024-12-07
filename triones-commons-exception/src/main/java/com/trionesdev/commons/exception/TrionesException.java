package com.trionesdev.commons.exception;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;

public class TrionesException extends RuntimeException {
    private static final long serialVersionUID = -5195341167826040681L;
    @Getter
    private String code;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public TrionesException() {
        super("triones exception");
    }

    public TrionesException(String code, Argument... params) {
        this( TrionesError.builder().code(code).arguments(params).build());
    }

    public TrionesException(String code, String defaultMessage, Argument... params) {
        this(TrionesError.builder().code(code).arguments(params).message(defaultMessage).build());
    }

    public TrionesException(Throwable cause, String code, Argument... params) {
        this(TrionesError.builder().cause(cause).code(code).arguments(params).build());
    }

    public TrionesException(TrionesError error) {
        super(error.getMessage(), error.getCause());
        this.code = error.getCode();
        Object[] args = new Object[]{};
        if (ArrayUtils.isNotEmpty(error.getArguments())) {
           args = Arrays.stream(error.getArguments()).map(Argument::getValue).toArray();
        }
        this.message = ExceptionResourceProperties.text(code, args);
        if (StringUtils.isBlank(this.message) && StringUtils.isNotBlank(error.getMessage())) {
            this.message = MessageFormat.format(error.getMessage(), args);
        }
    }
}
