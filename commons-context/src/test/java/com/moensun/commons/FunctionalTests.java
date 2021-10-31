package com.moensun.commons;


import com.moensun.commons.context.actor.Actor;
import com.moensun.commons.context.actor.ActorContextUtils;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class FunctionalTests {

    @Test
    public void runAsTest(){
        System.out.println(Thread.currentThread().getName());
        runAs(()->{
            System.out.println(Thread.currentThread().getName());
            System.out.println("1111");
        });


      String ss =  runAs(()->{
          System.out.println(Thread.currentThread().getName());
            return "sss";
        });

        ActorContextUtils.runAs(new Actor(),()->{
            System.out.println(Thread.currentThread().getName());
            System.out.println("1111");
        });
    }


    public void runAs(Runnable runnable){
        runnable.run();
    }

    public <T> T runAs(Supplier<T> supplier){
        return supplier.get();
    }

}
