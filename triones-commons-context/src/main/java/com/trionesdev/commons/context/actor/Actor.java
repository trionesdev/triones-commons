package com.trionesdev.commons.context.actor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * 代表系统中的参与者（Actor），如用户、系统等。
 */
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
    private String memberId;
    private String tenantId;
    @Deprecated
    private String tenantMemberId;
    private Instant time;
    private String ipAddress;
    private String userAgent;
    private String clientOs;
    private Map<String, Object> attributes;

}
