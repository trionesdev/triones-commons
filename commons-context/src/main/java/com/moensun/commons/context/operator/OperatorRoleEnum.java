package com.moensun.commons.context.operator;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public enum OperatorRoleEnum {
    USER,
    TENANT_USER,
    BOSS_USER;

    public static OperatorRoleEnum getByName(String val) {
        if (StringUtils.isNotBlank(val)) {
            for (OperatorRoleEnum item : OperatorRoleEnum.values()) {
                if (Objects.equals(val, item.name())) {
                    return item;
                }
            }

        }
        return null;
    }
}
