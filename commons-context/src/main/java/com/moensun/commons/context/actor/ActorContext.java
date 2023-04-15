package com.moensun.commons.context.actor;


import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
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
        return Optional.ofNullable(getActor()).map(Actor::getActorId).orElse(null);
    }

    public <T> T getActorId(Class<T> requiredType) {
        Actor actor = getActor();
        if (Objects.isNull(actor)) {
            return null;
        } else {
            return requiredType.cast(actor.getActorId());
        }
    }

    public String getUserId() {
        return Optional.ofNullable(getActor()).map(Actor::getUserId).orElse(null);
    }

    public String getTenantId() {
        return Optional.ofNullable(getActor()).map(Actor::getTenantId).orElse(null);
    }

    public String getMemberId() {
        return Optional.ofNullable(getActor()).map(Actor::getTenantMemberId).orElse(null);
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

    public boolean oneself(Object userId) {
        Object actorId = getActorId();
        if (Objects.isNull(actorId) || Objects.isNull(userId)) {
            return false;
        }
        return Objects.equals(actorId, userId);
    }

    public boolean hasPermission(Object operateId) {
        return oneself(operateId) || Objects.equals(getRole(), ActorRoleEnum.BOSS_USER.name());
    }

    public void resetActor() {
        ActorContextHolder.getActor();
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

