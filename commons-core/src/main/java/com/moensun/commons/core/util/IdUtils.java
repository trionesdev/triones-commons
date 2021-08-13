package com.moensun.commons.core.util;

import java.util.Objects;

public class IdUtils {
    public static boolean longIdValid(Long id){
        return Objects.nonNull(id) && !Objects.equals(0L,id);
    }
}
