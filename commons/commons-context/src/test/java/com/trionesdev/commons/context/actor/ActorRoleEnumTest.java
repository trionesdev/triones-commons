package com.trionesdev.commons.context.actor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActorRoleEnumTest {
    @Test
    void testGetByName() {
        assertEquals(ActorRoleEnum.USER, ActorRoleEnum.getByName("USER"));
        assertEquals(ActorRoleEnum.TENANT_MEMBER, ActorRoleEnum.getByName("TENANT_MEMBER"));
        assertEquals(ActorRoleEnum.BOSS_USER, ActorRoleEnum.getByName("BOSS_USER"));
        assertNull(ActorRoleEnum.getByName("NOT_EXIST"));
        assertNull(ActorRoleEnum.getByName(""));
        assertNull(ActorRoleEnum.getByName(null));
    }
} 