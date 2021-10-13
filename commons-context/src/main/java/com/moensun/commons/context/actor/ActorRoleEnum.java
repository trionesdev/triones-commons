package com.moensun.commons.context.actor;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public enum ActorRoleEnum {
    USER,
    TENANT_USER,
    BOSS_USER;

    public static ActorRoleEnum getByName(String val) {
        if (StringUtils.isNotBlank(val)) {
            for (ActorRoleEnum item : ActorRoleEnum.values()) {
                if (Objects.equals(val, item.name())) {
                    return item;
                }
            }

        }
        return null;
    }
}
