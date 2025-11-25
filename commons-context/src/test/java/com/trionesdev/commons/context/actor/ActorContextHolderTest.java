package com.trionesdev.commons.context.actor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActorContextHolderTest {
    @AfterEach
    void tearDown() {
        ActorContextHolder.resetActor();
    }

    @Test
    void testSetGetResetActor() {
        Actor actor = Actor.builder().actorId("a1").build();
        ActorContextHolder.setActor(actor);
        assertEquals("a1", ActorContextHolder.getActor().getActorId());
        ActorContextHolder.resetActor();
        assertNull(ActorContextHolder.getActor());
    }

    @Test
    void testRunAs() {
        Actor actor = Actor.builder().actorId("a2").build();
        ActorContextHolder.runAs(actor, () -> assertEquals("a2", ActorContextHolder.getActor().getActorId()));
        assertNull(ActorContextHolder.getActor());
    }

    @Test
    void testAddRemoveActorsFirst() {
        Actor a1 = Actor.builder().actorId("a1").build();
        Actor a2 = Actor.builder().actorId("a2").build();
        ActorContextHolder.setActor(a1);
        ActorContextHolder.addActorsFirst(a2);
        assertEquals("a2", ActorContextHolder.getActor().getActorId());
        ActorContextHolder.removeActorsFirst();
        assertEquals("a1", ActorContextHolder.getActor().getActorId());
    }
} 