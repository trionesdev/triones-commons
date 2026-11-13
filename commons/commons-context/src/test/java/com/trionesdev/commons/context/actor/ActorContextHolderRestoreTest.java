package com.trionesdev.commons.context.actor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class ActorContextHolderRestoreTest {

    @AfterEach
    void tearDown() {
        ActorContextHolder.resetActor();
        MDC.clear();
    }

    @Test
    void runAsRestoresActorWhenCallbackThrows() {
        ActorContextHolder.setActor(Actor.builder().userId("outer").build());
        assertThrows(IllegalStateException.class, () -> ActorContextHolder.runAs(
                Actor.builder().userId("inner").build(),
                (Runnable) () -> {
                    throw new IllegalStateException("boom");
                }));
        assertEquals("outer", ActorContextHolder.getActor().getUserId());
        assertEquals("outer", MDC.get(ActorConstants.MDC_USER_ID));
    }

    @Test
    void runAsWithoutCurrentActorRestoresEmptyState() {
        String result = ActorContextHolder.runAs(Actor.builder().userId("u1").build(),
                () -> ActorContextHolder.getActor().getUserId());
        assertEquals("u1", result);
        assertNull(ActorContextHolder.getActor());
        assertNull(MDC.get(ActorConstants.MDC_USER_ID));
    }

    @Test
    void nestedRunAsRestoresEachLevel() {
        ActorContextHolder.setActor(Actor.builder().userId("a").build());
        ActorContextHolder.runAs(Actor.builder().userId("b").build(), () -> {
            ActorContextHolder.runAs(Actor.builder().userId("c").build(),
                    () -> assertEquals("c", ActorContextHolder.getActor().getUserId()));
            assertEquals("b", ActorContextHolder.getActor().getUserId());
        });
        assertEquals("a", ActorContextHolder.getActor().getUserId());
    }

    @Test
    void runAsNullRunsWithoutActor() {
        ActorContextHolder.setActor(Actor.builder().userId("a").build());
        ActorContextHolder.runAs(null, () -> assertNull(ActorContextHolder.getActor()));
        assertEquals("a", ActorContextHolder.getActor().getUserId());
    }

    @Test
    void switchingActorDoesNotLeaveStaleMdcValues() {
        ActorContextHolder.setActor(Actor.builder().userId("u1").tenantId("t1").build());
        ActorContextHolder.runAs(Actor.builder().tenantId("t2").build(), () -> {
            assertNull(MDC.get(ActorConstants.MDC_USER_ID));
            assertEquals("t2", MDC.get(ActorConstants.MDC_TENANT_ID));
        });
        assertEquals("u1", MDC.get(ActorConstants.MDC_USER_ID));
    }

    @Test
    void resetKeepsForeignMdcKeys() {
        MDC.put("spanId", "s1");
        ActorContextHolder.setActor(Actor.builder().traceId("tr").userId("u1").build());
        ActorContextHolder.resetActor();
        assertNull(MDC.get(ActorConstants.MDC_TRACE_ID));
        assertNull(MDC.get(ActorConstants.MDC_USER_ID));
        assertEquals("s1", MDC.get("spanId"));
    }

    @Test
    void asTenantRunRestoresWhenCallbackThrows() {
        ActorContext context = new ActorContext();
        context.setActor(Actor.builder().tenantId("t1").build());
        assertThrows(IllegalStateException.class, () -> context.asTenant("t2").run(() -> {
            throw new IllegalStateException("boom");
        }));
        assertEquals("t1", context.getTenantId());
    }
}
