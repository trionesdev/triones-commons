package com.trionesdev.commons.exception;

import lombok.Data;

@Data
public class Argument {
    private Object value;
    public Argument(Object value){
        this.value = value;
    }

    public static Argument of(Object value){
        return new Argument(value);
    }

    public static Argument[] of(Object... value){
        Argument[] arguments = new Argument[value.length];
        for(int i = 0; i < value.length; i++){
            arguments[i] = of(value[i]);
        }
        return arguments;
    }
}
