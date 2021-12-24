package com.moensun.commons.context.actor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Actor implements Serializable {

    private static final long serialVersionUID = -2388323070328532598L;
    private String role;
    private String actorId;
    private String tenantId;
    private Instant time;

    @Override
    public String toString() {
        return "Actor{" +
                "role='" + role + '\'' +
                ", actorId='" + actorId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", time=" + time +
                '}';
    }
}
