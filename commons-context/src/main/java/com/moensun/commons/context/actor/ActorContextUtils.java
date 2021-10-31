package com.moensun.commons.context.actor;

import java.util.function.Supplier;

public class ActorContextUtils {


    public static void runAs(Actor actor,Runnable runnable){
        ActorContextHolder.runAs(actor,runnable);
    }

    public static <T> T runAs(Actor actor, Supplier<T> supplier){
        return ActorContextHolder.runAs(actor,supplier);
    }

}
