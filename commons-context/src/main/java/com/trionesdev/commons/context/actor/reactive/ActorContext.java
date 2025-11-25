package com.trionesdev.commons.context.actor.reactive;

import com.trionesdev.commons.context.actor.Actor;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Actor 上下文工具类
 */
public class ActorContext {
    /**
     * 获取当前上下文中的 Actor
     */
    public static Mono<Actor> getActor() {
        return ActorContextHolder.getActor();
    }

    /**
     * 在上下文中设置 Actor
     */
    public static Context setActor(Context context, Actor actor) {
        return ActorContextHolder.setActor(context, actor);
    }

    /**
     * 获取当前上下文中的 ActorId
     */
    public static Mono<String> getActorId() {
        return getActor().map(Actor::getActorId);
    }
}
