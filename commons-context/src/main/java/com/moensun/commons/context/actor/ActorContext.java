package com.moensun.commons.context.actor;


import java.time.Instant;
import java.util.Objects;

public class ActorContext {
    public Actor getActor(){
        return ActorContextHolder.getActor();
    }

    public  void  setActor(Actor operate){
        ActorContextHolder.setActor(operate);
    }

    public ActorRoleEnum getRole(){
        Actor actor = getActor();
        if(Objects.isNull(actor)){
            return null;
        }else {
            return actor.getRole();
        }
    }

    public Object getActorId(){
        Actor actor = getActor();
        if(Objects.isNull(actor)){
            return null;
        }else {
            return actor.getActorId();
        }
    }

    public  <T> T getActorId(Class<T> requiredType){
        Actor actor = getActor();
        if(Objects.isNull(actor)){
            return null;
        }else {
            return requiredType.cast(actor.getActorId());
        }
    }

    public Object getTenantId(){
        Actor actor = getActor();
        if(Objects.isNull(actor)){
            return null;
        }else {
            return actor.getTenantId();
        }
    }

    public Instant getTime(){
        Actor actor = getActor();
        if(Objects.isNull(actor) || Objects.isNull(actor.getTime())){
            return Instant.now();
        }else {
            return actor.getTime();
        }
    }

    public String getRoleName(){
        Actor actor = getActor();
        if(Objects.isNull(actor) || Objects.isNull(actor.getRole())){
            return null;
        }else {
            return actor.getRole().name();
        }
    }

    public Long getActorIdAsLong(){
        Object operatorId = getActorId();
        return Objects.isNull(operatorId)? null : Long.valueOf(String.valueOf(operatorId));
    }

    public Long getTenantIdAsLong(){
        Object tenantId = getTenantId();
        return Objects.isNull(tenantId)? null : Long.valueOf(String.valueOf(tenantId));
    }

    public String getTimeAsString(){
        Instant date = getTime();
        return Objects.nonNull(date)? String.valueOf(date.getEpochSecond()) : null;
    }

    public boolean oneself(Object userId){
        Object actorId = getActorId();
        if(Objects.isNull(actorId) || Objects.isNull(userId)){
            return false;
        }
        return Objects.equals(actorId,userId);
    }

    public boolean hasPermission(Object operateId){
        return oneself(operateId) || Objects.equals(getRole(), ActorRoleEnum.BOSS_USER);
    }

    public void resetActor(){
        ActorContextHolder.getActor();
    }

}

