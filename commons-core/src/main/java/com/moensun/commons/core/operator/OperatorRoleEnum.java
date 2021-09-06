package com.moensun.commons.core.operator;

import com.google.common.base.Strings;

import java.util.Objects;

public enum OperatorRoleEnum {
    USER,
    TENANT_USER,
    BOSS_USER;

    public static OperatorRoleEnum getByName(String val) {
        if (!Strings.isNullOrEmpty(val)) {
            for (OperatorRoleEnum item : OperatorRoleEnum.values()) {
                if (Objects.equals(val, item.name())) {
                    return item;
                }
            }

        }
        return null;
    }
}
