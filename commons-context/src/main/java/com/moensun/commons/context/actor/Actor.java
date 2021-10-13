package com.moensun.commons.context.actor;

import lombok.Data;

import java.time.Instant;

@Data
public class Actor {
    private String role;
    private String actorId;
    private String tenantId;
    private Instant time;
}
