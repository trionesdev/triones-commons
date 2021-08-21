package com.moensun.commons.core.util;

import java.util.Objects;

<<<<<<< HEAD:commons-core/src/main/java/com/moensun/commons/core/util/IdentityUtils.java
public class IdentityUtils {
=======
public class IdUtils {
>>>>>>> origin/master:commons-core/src/main/java/com/moensun/commons/core/util/IdUtils.java
    public static boolean longIdValid(Long id){
        return Objects.nonNull(id) && !Objects.equals(0L,id);
    }
}
