package com.moensun.commons;

import com.moensun.commons.context.actor.Actor;
import com.moensun.commons.context.actor.ActorContextHolder;
import org.junit.jupiter.api.Test;

public class ActorContextHolderUtils {

    @Test
    public void ss(){

        Actor actor = Actor.builder().actorId("1").build();
        ActorContextHolder.setActor(actor);

        Actor actor2 = Actor.builder().actorId("2").build();
        ActorContextHolder.runAs(actor2,()->{
            System.out.println(ActorContextHolder.getActor());
        });

        System.out.println(ActorContextHolder.getActor());
    }

}
