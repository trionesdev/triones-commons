package com.moensun.commons.context.operator;

import lombok.Data;

import java.time.Instant;

@Data
public class Operator {
    private OperatorRoleEnum operatorRole;
    private Object operatorId;
    private Object tenantId;
    private Instant time;
}