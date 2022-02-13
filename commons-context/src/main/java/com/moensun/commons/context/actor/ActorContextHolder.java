package com.moensun.commons.context.actor;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.moensun.commons.opentracing.util.BaggageUtils;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopSpan;
import io.opentracing.util.GlobalTracer;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Supplier;

import static com.moensun.commons.context.actor.ActorConstants.*;

public class ActorContextHolder {
    private static final Logger logger = LoggerFactory.getLogger(ActorContextHolder.class);
    private ActorContextHolder() {
    }

    private static final  TransmittableThreadLocal<Actor> actorHolder = new TransmittableThreadLocal<>();
    private static final  TransmittableThreadLocal<LinkedBlockingDeque<Actor>> actorQueue = new TransmittableThreadLocal<>();

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

    public static void addActorsFirst(Actor actor){
        if(Objects.isNull(actor)){
            return;
        }
        LinkedBlockingDeque<Actor> actors = actorQueue.get();
        if(Objects.isNull(actors)){
            actors = new LinkedBlockingDeque<>();
        }
        Actor previousActor = getActor();
        actors.addFirst(previousActor);
        actorQueue.set(actors);
        setActor(actor);
    }

    public static void removeActorsFirst(){
        LinkedBlockingDeque<Actor> actors = actorQueue.get();
        if(Objects.isNull(actors)){
            setActor(null);
            return ;
        }
        try{
            Actor previousActor = actors.removeFirst();
            actorQueue.set(actors);
            setActor(previousActor);
        }catch (NoSuchElementException ex){
            if(logger.isDebugEnabled()){
                logger.info("[ActorContextHolder] context actors is empty ");
            }
        }
    }

    public static void runAs(Actor actor,Runnable runnable){
        addActorsFirst(actor);
        runnable.run();
        removeActorsFirst();
    }

    public static <T> T runAs(Actor actor, Supplier<T> supplier){
        addActorsFirst(actor);
        T result = supplier.get();
        removeActorsFirst();
        return result;
    }

    public static boolean hasTracer(){
        return !(GlobalTracer.get().activeSpan() instanceof NoopSpan);
    }

    private static Actor getLocalActor(){
        return actorHolder.get();
    }

    private static Actor getTracerActor(){
        Span activeSpan = GlobalTracer.get().scopeManager().activeSpan();
        if(Objects.isNull(activeSpan)){
            return null;
        }
        Actor actor = Actor.builder().build();
        actor.setActorId(activeSpan.getBaggageItem(BaggageUtils.itemKey(X_ACTOR_ID)));
        actor.setTenantId(activeSpan.getBaggageItem(BaggageUtils.itemKey(X_TENANT_ID)));
        actor.setRole(activeSpan.getBaggageItem(BaggageUtils.itemKey(X_ROLE)));
        return actor;
    }

    private static void setLocalActor(Actor actor){
        actorHolder.set(actor);
        putLocalMDCContext(actor);
    }

    private static void setTracerActor(Actor actor){
        ScopeManager scopeManager = GlobalTracer.get().scopeManager();
        Span parentSpan = scopeManager.activeSpan();
        Tracer.SpanBuilder spanBuilder = GlobalTracer.get().buildSpan("set-actor");
        Span actorSpan ;
        if(Objects.nonNull(parentSpan)){
            actorSpan = spanBuilder.asChildOf(parentSpan).start();
        }else {
            actorSpan = spanBuilder.start();
        }
        actorSpan.setBaggageItem(BaggageUtils.itemKey(X_ACTOR_ID), actor.getActorId());
        actorSpan.setBaggageItem(BaggageUtils.itemKey(X_TENANT_ID), actor.getTenantId());
        actorSpan.setBaggageItem(BaggageUtils.itemKey(X_ROLE), actor.getRole());
        scopeManager.activate(actorSpan);
        actorSpan.finish();
    }

    private static void resetLocalActor(){
        actorHolder.remove();
        cleanLocalMDCContext();
    }

    private static void resetTracerActor(){
        Span activeSpan = GlobalTracer.get().activeSpan();
        if(Objects.isNull(activeSpan)){
            return;
        }
        activeSpan.setBaggageItem(X_ACTOR_ID,null);
        activeSpan.setBaggageItem(X_TENANT_ID,null);
        activeSpan.setBaggageItem(X_ROLE,null);
    }

    public static class ActorWrapper{
        private Actor actor;
        private final LinkedBlockingDeque<Actor> actors = new LinkedBlockingDeque<>();

        public ActorWrapper(Actor actor) {
            this.actor = actor;
        }



        public void add(Actor newActor){
            if(Objects.nonNull(actor)){
                actors.addFirst(actor);
            }
            actor = newActor;
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

    private static void putLocalMDCContext(Actor actor){
        if(Objects.nonNull(actor)){
            MDC.put(MDC_ACTOR_ID,actor.getActorId());
            MDC.put(MDC_TENANT_ID,actor.getTenantId());
            MDC.put(MDC_ROLE,actor.getRole());
        }
    }

    private static void cleanLocalMDCContext(){
        MDC.put(MDC_ACTOR_ID,null);
        MDC.put(MDC_TENANT_ID,null);
        MDC.put(MDC_ROLE,null);
    }

}
