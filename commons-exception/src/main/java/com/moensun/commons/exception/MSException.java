package com.moensun.commons.exception;

public class MSException extends RuntimeException{
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public MSException(){
        super("");
    }

    public MSException(String message) {
        super(message);
        this.message = message;
    }

    public MSException(String message, Throwable cause) {
        super(message, cause);
        this.code = null;
        this.message = message;
    }

    public MSException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public MSException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public MSException(String message, String code, String message1) {
        super(message);
        this.code = code;
        this.message = message1;
    }

    public MSException(String message, Throwable cause, String code, String message1) {
        super(message, cause);
        this.code = code;
        this.message = message1;
    }

    public MSException(Throwable cause, String code, String message) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public MSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code, String message1) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.message = message1;
    }
}
