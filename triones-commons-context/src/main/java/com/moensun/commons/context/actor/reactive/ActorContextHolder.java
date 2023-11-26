package com.moensun.commons.context.actor.reactive;

import com.moensun.commons.context.actor.Actor;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.NoSuchElementException;
import java.util.Objects;

public class ActorContextHolder {
    public static Mono<Actor> getActor() {
        return Mono.deferContextual(contextView -> {
            try {
                Actor actor = contextView.get(Actor.class);
                return Mono.just(actor);
            } catch (NoSuchElementException ex) {
                return Mono.empty();
            }
        });
    }

    public static Context setActor(Context context, Actor actor) {
        if(Objects.nonNull(actor)){
            return context.put(Actor.class, actor);
        }else {
            return context;
        }
    }
}
