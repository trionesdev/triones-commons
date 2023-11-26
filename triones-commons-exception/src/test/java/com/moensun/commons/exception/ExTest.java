package com.moensun.commons.exception;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ExTest {

    public void notFoundEx(){

    }

    @Test
    public void triones_test(){
        Map<String,String> tmap = new HashMap<>();
        tmap.put("TEST","ce shi");
        ExceptionResourceProperties.codeMap.put(Locale.getDefault(),tmap );
        throw new TrionesException("TEST");
    }

    @Test
    public void triones_test2(){
        Map<String,String> tmap = new HashMap<>();
        tmap.put("TEST","ce shi");
        ExceptionResourceProperties.codeMap.put(Locale.getDefault(),tmap );
        throw new TrionesException("TEST","test");
    }

}
