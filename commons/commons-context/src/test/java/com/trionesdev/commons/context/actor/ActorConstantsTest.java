package com.trionesdev.commons.context.actor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActorConstantsTest {
    @Test
    void testConstants() {
        assertEquals("X-Request-TraceId", ActorConstants.X_TRACE_ID);
        assertEquals("X-Request-ActorId", ActorConstants.X_ACTOR_ID);
        assertEquals("X-Request-UserId", ActorConstants.X_USER_ID);
        assertEquals("X-Request-TenantId", ActorConstants.X_TENANT_ID);
        assertEquals("X-Request-Tenant-MemberId", ActorConstants.X_TENANT_MEMBER_ID);
        assertEquals("X-Request-MemberId", ActorConstants.X_MEMBER_ID);
        assertEquals("X-Request-Role", ActorConstants.X_ROLE);
        assertEquals("X-Request-Time", ActorConstants.X_TIME);
        assertEquals("traceId", ActorConstants.MDC_TRACE_ID);
        assertEquals("actorId", ActorConstants.MDC_ACTOR_ID);
        assertEquals("userId", ActorConstants.MDC_USER_ID);
        assertEquals("tenantId", ActorConstants.MDC_TENANT_ID);
        assertEquals("tenantMemberId", ActorConstants.MDC_TENANT_MEMBER_ID);
        assertEquals("role", ActorConstants.MDC_ROLE);
    }
} 