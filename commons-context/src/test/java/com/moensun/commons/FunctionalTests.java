package com.moensun.commons;

import org.junit.Test;

import java.util.function.Supplier;

public class FunctionalTests {

    @Test
    public void runAsTest(){
        System.out.println(Thread.currentThread().getName());
        runAs(()->{
            System.out.println(Thread.currentThread().getName());
            System.out.println("1111");});


      String ss =  runAs(()->{
          System.out.println(Thread.currentThread().getName());
            return "sss";
        });
    }


    public void runAs(Runnable runnable){
        runnable.run();
    }

    public <T> T runAs(Supplier<T> supplier){
        return supplier.get();
    }

}
