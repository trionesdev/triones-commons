package com.trionesdev.commons.context.actor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ActorContextTest {
    private final ActorContext context = new ActorContext();

    @AfterEach
    void tearDown() {
        context.resetActor();
    }

    @Test
    void testSetGetActor() {
        Actor actor = Actor.builder().actorId("a1").role("USER").userId("u1").tenantId("t1").tenantMemberId("tm1").build();
        context.setActor(actor);
        assertEquals(actor, context.getActor());
    }

    @Test
    void testGetRoleAndIds() {
        Actor actor = Actor.builder().actorId("a1").role("USER").userId("u1").tenantId("t1").tenantMemberId("tm1").build();
        context.setActor(actor);
        assertEquals("USER", context.getRole());
        assertEquals("a1", context.getActorId());
        assertEquals("u1", context.getUserId());
        assertEquals("t1", context.getTenantId());
        assertEquals("tm1", context.getMemberId());
    }

    @Test
    void testGetAttributes() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("k", "v");
        Actor actor = Actor.builder().actorId("a1").attributes(attrs).build();
        context.setActor(actor);
        assertEquals("v", context.getAttributes().get("k"));
    }

    @Test
    void testIsEmpty() {
        assertTrue(context.isEmpty());
        Actor actor = Actor.builder().actorId("a1").build();
        context.setActor(actor);
        assertFalse(context.isEmpty());
    }

    @Test
    void testGetTime() {
        Instant now = Instant.now();
        Actor actor = Actor.builder().actorId("a1").time(now).build();
        context.setActor(actor);
        assertEquals(now, context.getTime());
        context.setActor(null);
        assertNotNull(context.getTime());
    }

    @Test
    void testOneselfAndHasPermission() {
        Actor actor = Actor.builder().actorId("a1").role("USER").build();
        context.setActor(actor);
        assertTrue(context.oneself("a1"));
        assertFalse(context.oneself("a2"));
        assertFalse(context.hasPermission("a2"));
        context.setActor(Actor.builder().actorId("b1").role("BOSS_USER").build());
        assertTrue(context.hasPermission("any"));
    }

    @Test
    void testRunAsAndRun() {
        Actor actor = Actor.builder().actorId("a1").build();
        context.runAs(actor, () -> assertEquals("a1", context.getActor().getActorId()));
        assertNull(context.getActor());
        String result = context.runAs(actor, () -> context.getActor().getActorId());
        assertEquals("a1", result);
        assertNull(context.getActor());
        context.asTenant("t2");
        assertEquals("t2", context.getTenantId());
        context.run(() -> assertNotNull(context.getActor()));
        assertNull(context.getActor());
    }
} 