package com.moensun.commons.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class IdentityUtils {

    public static boolean longIdValid(Long id){
        return Objects.nonNull(id) && !Objects.equals(0L,id);
    }

    public static boolean stringIdValid(String id){
        return StringUtils.isNotBlank(id) && !Objects.equals("0",id);
    }

}
