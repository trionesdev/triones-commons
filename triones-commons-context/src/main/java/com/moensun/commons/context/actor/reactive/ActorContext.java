package com.moensun.commons.context.actor.reactive;

import com.moensun.commons.context.actor.Actor;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ActorContext {
    public Mono<Actor> getActor() {
        return ActorContextHolder.getActor();
    }

    public Context setActor(Context context, Actor actor) {
        return ActorContextHolder.setActor(context, actor);
    }

    public Mono<String> getActorId() {
        return ActorContextHolder.getActor().map(Actor::getActorId);
    }

}
