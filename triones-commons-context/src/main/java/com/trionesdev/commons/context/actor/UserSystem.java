package com.trionesdev.commons.context.actor;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public enum UserSystem {
    USER,//个人用户
    TENANT, //租户
    ADMIN,//管理人员
    AGENT;

    public static UserSystem getByName(String val) {
        if (StringUtils.isNotBlank(val)) {
            for (UserSystem item : UserSystem.values()) {
                if (Objects.equals(val, item.name())) {
                    return item;
                }
            }

        }
        return null;
    }
}
