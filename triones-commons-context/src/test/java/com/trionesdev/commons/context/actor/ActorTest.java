package com.trionesdev.commons.context.actor;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ActorTest {
    @Test
    void testBuilderAndGetter() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("k", "v");
        Actor actor = Actor.builder()
                .role("USER")
                .actorId("a1")
                .userId("u1")
                .tenantId("t1")
                .tenantMemberId("tm1")
                .time(Instant.now())
                .attributes(attrs)
                .build();
        assertEquals("USER", actor.getRole());
        assertEquals("a1", actor.getActorId());
        assertEquals("u1", actor.getUserId());
        assertEquals("t1", actor.getTenantId());
        assertEquals("tm1", actor.getTenantMemberId());
        assertEquals("v", actor.getAttributes().get("k"));
    }

    @Test
    void testSerializable() throws Exception {
        Actor actor = Actor.builder().actorId("a1").build();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(actor);
        out.flush();
        byte[] bytes = bos.toByteArray();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Actor deserialized = (Actor) in.readObject();
        assertEquals("a1", deserialized.getActorId());
    }
} 