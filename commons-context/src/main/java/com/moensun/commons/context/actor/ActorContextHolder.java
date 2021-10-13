package com.moensun.commons.context.actor;

import com.alibaba.ttl.TransmittableThreadLocal;

public class ActorContextHolder {
    private ActorContextHolder() {
    }

    private static final  TransmittableThreadLocal<Actor> actorHolder = new TransmittableThreadLocal<>();

    public static void resetActor() {
        actorHolder.remove();
    }

    public static void setActor(Actor actor) {
        if (actor == null) {
            resetActor();
        } else {
            actorHolder.set(actor);
        }
    }

    public static Actor getActor() {
        return actorHolder.get();
    }

}
