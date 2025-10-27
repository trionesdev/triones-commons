package com.trionesdev.commons.context.actor;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

public class ActorContext {
    public Actor getActor() {
        return ActorContextHolder.getActor();
    }

    public void setActor(Actor operate) {
        ActorContextHolder.setActor(operate);
    }

    public String getRole() {
        return Optional.ofNullable(getActor()).map(Actor::getRole).orElse(null);
    }

    public String getActorId() {
        return Optional.ofNullable(getActor()).map(actor -> {
            if (StringUtils.isNotBlank(actor.getActorId())) {
                return actor.getActorId();
            } else {
                return null;
            }
        }).orElse(null);
    }

    public <T> T getActorId(Class<T> requiredType) {
        Actor actor = getActor();
        if (Objects.isNull(actor)) {
            return null;
        } else {
            return requiredType.cast(getActorId());
        }
    }

    public String getUserId() {
        return Optional.ofNullable(getActor()).map(Actor::getUserId).orElse(null);
    }

    public String getTenantId() {
        return Optional.ofNullable(getActor()).map(Actor::getTenantId).orElse(null);
    }

    public String getMemberId() {
        return Optional.ofNullable(getActor()).map(Actor::getMemberId).orElse(null);
    }

    public Map<String, Object> getAttributes() {
        return Optional.ofNullable(getActor()).map(Actor::getAttributes).orElse(new HashMap<>());
    }

    public Boolean isEmpty() {
        return Optional.ofNullable(getActor()).map(actor -> {
            return Objects.isNull(actor.getActorId()) && Objects.isNull(actor.getMemberId()) && Objects.isNull(actor.getUserId());
        }).orElse(true);
    }

    public Instant getTime() {
        Actor actor = getActor();
        if (Objects.isNull(actor) || Objects.isNull(actor.getTime())) {
            return Instant.now();
        } else {
            return actor.getTime();
        }
    }

    public String getTimeAsString() {
        Instant date = getTime();
        return Objects.nonNull(date) ? String.valueOf(date.toEpochMilli()) : null;
    }

    public boolean oneself(Object operatorId){
        if (Objects.isNull(operatorId)){
            return false;
        }
        ActorRoleEnum role = ActorRoleEnum.getByName(getRole());
        switch (Objects.requireNonNull(role)){
            case PERSONAL_USER:
                return Objects.equals(operatorId, getUserId());
            case TENANT_USER:
            case TENANT_MEMBER:
                return Objects.equals(operatorId, getMemberId());
            case ADMIN_USER:
                return true;
            case AGENT_USER:
                return true;
            default:
                return false;
        }
    }

    @Deprecated
    public boolean actorOneself(Object operatorId) {
        Object actorId = getActorId();
        if (Objects.isNull(actorId) || Objects.isNull(operatorId)) {
            return false;
        }
        return Objects.equals(actorId, operatorId);
    }

    @Deprecated
    public boolean userOneself(Object operatorId) {
        Object userId = getUserId();
        if (Objects.isNull(userId) || Objects.isNull(operatorId)) {
            return false;
        }
        return Objects.equals(userId, operatorId);
    }

    @Deprecated
    public boolean memberOneself(Object operatorId) {
        Object memberId = getMemberId();
        if (Objects.isNull(memberId) || Objects.isNull(operatorId)) {
            return false;
        }
        return Objects.equals(memberId, operatorId);
    }

    @Deprecated
    public boolean hasActorPermission(Object... operateIds) {
        return ArrayUtils.contains(operateIds, getActorId()) || Objects.equals(getRole(), ActorRoleEnum.BOSS_USER.name());
    }

    @Deprecated
    public boolean hasUserPermission(Object... operateIds) {
        return ArrayUtils.contains(operateIds, getUserId()) || Objects.equals(getRole(), ActorRoleEnum.BOSS_USER.name());
    }

    @Deprecated
    public boolean hasMemberPermission(Object... operateIds) {
        return ArrayUtils.contains(operateIds, getMemberId()) || Objects.equals(getRole(), ActorRoleEnum.BOSS_USER.name());
    }


    public void resetActor() {
        ActorContextHolder.resetActor();
    }


    public void runAs(Actor actor, Runnable runnable) {
        ActorContextHolder.runAs(actor, runnable);
    }

    public <T> T runAs(Actor actor, Supplier<T> supplier) {
        return ActorContextHolder.runAs(actor, supplier);
    }

    public ActorContext asTenant(String tenantId) {
        Actor actor = Actor.builder().tenantId(tenantId).build();
        ActorContextHolder.addActorsFirst(actor);
        return this;
    }

    public void run(Runnable runnable) {
        runnable.run();
        ActorContextHolder.removeActorsFirst();
    }

}

