package com.moensun.commons.exception;

public class TrionesException extends RuntimeException{
    private static final long serialVersionUID = -5195341167826040681L;
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public TrionesException(){
        super("");
    }

    public TrionesException(String message) {
        super(message);
        this.message = message;
    }

    public TrionesException(String message, Throwable cause) {
        super(message, cause);
        this.code = null;
        this.message = message;
    }

    public TrionesException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public TrionesException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public TrionesException(Throwable cause, String code, String message) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public TrionesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.message = message;
    }

    public TrionesException(TrionesError error){
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
    }
}
