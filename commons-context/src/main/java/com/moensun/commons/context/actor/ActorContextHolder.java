package com.moensun.commons.context.actor;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.opentracing.Span;
import io.opentracing.noop.NoopSpan;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Supplier;

import static com.moensun.commons.context.actor.ActorConstants.X_ACTOR_ID;
import static com.moensun.commons.context.actor.ActorConstants.X_TENANT_ID;

public class ActorContextHolder {
    private static final Logger logger = LoggerFactory.getLogger(ActorContextHolder.class);
    private ActorContextHolder() {
    }

    private static final  TransmittableThreadLocal<Actor> actorHolder = new TransmittableThreadLocal<>();

    public static void setActor(Actor actor) {
        if(hasTracer()){
            setTracerActor(actor);
        }else {
            setLocalActor(actor);
        }
    }

    public static Actor getActor() {
        if(hasTracer()){
            return getTracerActor();
        }else {
            return getLocalActor();
        }
    }

    public static void resetActor() {
        if(hasTracer()){
            resetTracerActor();
        }else {
            resetLocalActor();
        }
    }

    public static void runAs(Actor actor,Runnable runnable){
        Actor previousActor = runAsBefore(actor);
        runnable.run();
        runAsAfter(previousActor);
    }

    public static <T> T runAs(Actor actor, Supplier<T> supplier){
        Actor previousActor = runAsBefore(actor);
        T result = supplier.get();
        runAsAfter(previousActor);
        return result;
    }

    private static boolean hasTracer(){
        return !(GlobalTracer.get().activeSpan() instanceof NoopSpan);
    }

    private static Actor getLocalActor(){
        return actorHolder.get();
    }

    private static Actor getTracerActor(){
        Span activeSpan = GlobalTracer.get().activeSpan();
        if(Objects.isNull(activeSpan)){
            return null;
        }
        Actor actor = Actor.builder().build();
        actor.setActorId(activeSpan.getBaggageItem(X_ACTOR_ID));
        actor.setTenantId(activeSpan.getBaggageItem(X_TENANT_ID));
        return actor;
    }

    private static void setLocalActor(Actor actor){
        actorHolder.set(actor);
    }

    private static void setTracerActor(Actor actor){
        boolean hasTracer = false;
        Span activeSpan = GlobalTracer.get().activeSpan();
        if(Objects.isNull(activeSpan)){
            activeSpan = GlobalTracer.get().buildSpan("set-actor").start();
            GlobalTracer.get().activateSpan(activeSpan);
        }else {
            hasTracer = true;
        }
        activeSpan.setBaggageItem(X_ACTOR_ID, actor.getActorId());
        activeSpan.setBaggageItem(X_TENANT_ID, actor.getTenantId());
        if(!hasTracer){
            activeSpan.finish();
        }
    }

    private static void resetLocalActor(){
        actorHolder.remove();
    }

    private static void resetTracerActor(){
        Span activeSpan = GlobalTracer.get().activeSpan();
        if(Objects.isNull(activeSpan)){
            return;
        }
        activeSpan.setBaggageItem(X_ACTOR_ID,null);
        activeSpan.setBaggageItem(X_TENANT_ID,null);
    }

    private static Actor runAsBefore(Actor actor){
        Actor previousActor = getActor();
        resetActor();
        setActor(actor);
        return previousActor;
    }

    private static void runAsAfter(Actor previousActor){
        resetActor();
        setActor(previousActor);
    }

    public static class ActorWrapper{
        private Actor actor;
        private final LinkedBlockingDeque<Actor> actors = new LinkedBlockingDeque<>();

        public ActorWrapper(Actor actor) {
            this.actor = actor;
        }



        public void add(Actor newOperator){
            if(Objects.nonNull(actor)){
                actors.addFirst(actor);
            }
            actor = newOperator;
        }

        public void remove(){
            actor = null;
            if(actors.isEmpty()){
                return;
            }
            try{
                actor = actors.removeFirst();
            }catch (NoSuchElementException ex){
                if(logger.isDebugEnabled()){
                    logger.info("[ActorContextHolder] context actors is empty ");
                }
            }
        }
    }
}
