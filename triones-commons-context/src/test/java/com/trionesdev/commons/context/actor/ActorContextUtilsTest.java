package com.trionesdev.commons.context.actor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActorContextUtilsTest {
    @AfterEach
    void tearDown() {
        ActorContextHolder.resetActor();
    }

    @Test
    void testRunAsRunnable() {
        Actor actor = Actor.builder().actorId("a1").build();
        ActorContextUtils.runAs(actor, () -> assertEquals("a1", ActorContextHolder.getActor().getActorId()));
        assertNull(ActorContextHolder.getActor());
    }

    @Test
    void testRunAsSupplier() {
        Actor actor = Actor.builder().actorId("a2").build();
        String result = ActorContextUtils.runAs(actor, () -> ActorContextHolder.getActor().getActorId());
        assertEquals("a2", result);
        assertNull(ActorContextHolder.getActor());
    }
} 