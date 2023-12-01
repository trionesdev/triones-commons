package com.trionesdev.commons.core.util;

import com.trionesdev.commons.core.constant.IdentityConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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
        return Objects.nonNull(id) && !Objects.equals(IdentityConstants.LONG_ID_ZERO_VALUE, id);
    }

    /**
     * judge if the integer identity is valid
     * @param id
     * @return
     */
    public static boolean intIdValid(Integer id) {
        return Objects.nonNull(id) && !Objects.equals(IdentityConstants.INTEGER_ID_ZERO_VALUE, id);
    }

    /**
     * judge if the string identity is valid
     * @param id
     * @return
     */
    public static boolean stringIdValid(String id) {
        return StringUtils.isNotBlank(id) && !Objects.equals(IdentityConstants.STRING_ID_ZERO_VALUE, id);
    }

}
