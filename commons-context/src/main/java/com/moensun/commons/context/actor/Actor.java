package com.moensun.commons.context.actor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Actor {
    private String role;
    private String actorId;
    private String tenantId;
    private Instant time;
}
