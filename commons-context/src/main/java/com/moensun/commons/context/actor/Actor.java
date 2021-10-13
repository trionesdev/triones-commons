package com.moensun.commons.context.actor;

import lombok.Data;

import java.time.Instant;

@Data
public class Actor {
    private ActorRoleEnum role;
    private Object actorId;
    private Object tenantId;
    private Instant time;
}
