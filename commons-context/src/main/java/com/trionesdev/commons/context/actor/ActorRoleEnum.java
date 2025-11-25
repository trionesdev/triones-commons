package com.trionesdev.commons.context.actor;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public enum ActorRoleEnum {
    /**
     * USER si deprecated ,use PERSONAL_USER inside.
     */
    @Deprecated
    USER,
    PERSONAL_USER,//个人用户
    TENANT_USER,
    TENANT_MEMBER, //租户
    ADMIN_USER,//管理人员
    AGENT_USER,//代理商
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
