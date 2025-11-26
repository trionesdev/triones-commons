package com.trionesdev.commons.exception;

import lombok.Data;

@Data
public class ArgumentValue {
    private Object value;
    public ArgumentValue(Object value){
        this.value = value;
    }

    public static ArgumentValue of(Object value){
        return new ArgumentValue(value);
    }

    public static ArgumentValue[] of(Object... value){
        ArgumentValue[] arguments = new ArgumentValue[value.length];
        for(int i = 0; i < value.length; i++){
            arguments[i] = of(value[i]);
        }
        return arguments;
    }
}
