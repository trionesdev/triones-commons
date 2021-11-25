package com.moensun.commons.opentracing.util;

import java.util.Objects;

public class BaggageUtils {

    public static String itemKey(String key){
        if(Objects.isNull(key)){
            return null;
        }
        return key.toLowerCase();
    }

}
