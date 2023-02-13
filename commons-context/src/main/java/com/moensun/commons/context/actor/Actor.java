package com.moensun.commons.context.actor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@ToString
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Actor implements Serializable {

    private static final long serialVersionUID = -2388323070328532598L;
    private String role;
    private String actorId;
    private String userId;
    private String tenantId;
    private String tenantMemberId;
    private Instant time;

    public String getActorId() {
        return Objects.equals(ActorRoleEnum.TENANT_USER.name(), this.role) ? tenantMemberId : userId;
    }
}
