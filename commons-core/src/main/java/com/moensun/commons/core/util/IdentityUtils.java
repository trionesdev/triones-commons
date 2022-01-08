package com.moensun.commons.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static com.moensun.commons.core.constant.IdentityConstants.*;

/**
 * @author bane.shi
 */
public class IdentityUtils {

    /**
     * judge if the long type identity id valid
     * @param id
     * @return
     */
    public static boolean longIdValid(Long id) {
        return Objects.nonNull(id) && !Objects.equals(DEFAULT_NULL_LONG_ID, id);
    }

    /**
     * judge if the integer identity is valid
     * @param id
     * @return
     */
    public static boolean intIdValid(Integer id) {
        return Objects.nonNull(id) && !Objects.equals(DEFAULT_NULL_INTEGER_ID, id);
    }

    /**
     * judge if the string identity is valid
     * @param id
     * @return
     */
    public static boolean stringIdValid(String id) {
        return StringUtils.isNotBlank(id) && !Objects.equals(DEFAULT_NULL_STRING_ID, id);
    }

}
