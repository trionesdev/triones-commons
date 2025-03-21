package com.trionesdev.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ActorProfile {
    private String nickname;
    private String userId;
    private String role;
    private String memberId;
    private String avatar;
    private String gender;
    private String email;
    private String tenantId;
    private String tenantName;
}
