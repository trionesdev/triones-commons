package com.trionesdev.commons.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
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

    @Test
    public void triones_test3(){
        try {
            ExceptionResourceProperties.setResourcePaths("i18n/error");
            throw new TrionesException("USER_NOT_FOUND");
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
        }
    }

}
