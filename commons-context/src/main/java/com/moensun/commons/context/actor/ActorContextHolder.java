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

import static com.moensun.commons.context.actor.ActorConstants.X_ACTOR_ID;
import static com.moensun.commons.context.actor.ActorConstants.X_TENANT_ID;

public class ActorContextHolder {
    private static final Logger logger = LoggerFactory.getLogger(ActorContextHolder.class);
    private ActorContextHolder() {
    }

    private static final  TransmittableThreadLocal<ActorWrapper> actorHolder = new TransmittableThreadLocal<>();

    public static void setActor(Actor actor) {
        if(hasTracer()){
            setTracerActor(actor);
        }else {
            setLocalActor(actor);
        }
    }

    public static Actor getActor() {
        if(hasTracer()){
            return getActorFromTracer();
        }else {
            return getActorFromLocal();
        }
    }

    public static void resetActor() {
        if(hasTracer()){
            resetTracerActor();
        }else {
            resetLocalActor();
        }
    }

    public static void removeActor() {
        if(hasTracer()){
            resetTracerActor();
        }else {
            removeLocalActor();
        }
    }

    private static boolean hasTracer(){
        return !(GlobalTracer.get().activeSpan() instanceof NoopSpan);
    }

    private static Actor getActorFromLocal(){
        ActorWrapper actorWrapper = actorHolder.get();
        if(Objects.isNull(actorWrapper)){
            return null;
        }
        return actorWrapper.actor;
    }

    private static Actor getActorFromTracer(){
        Span activeSpan = GlobalTracer.get().activeSpan();
        Actor actor = Actor.builder().build();
        actor.setActorId(activeSpan.getBaggageItem(X_ACTOR_ID));
        actor.setTenantId(activeSpan.getBaggageItem(X_TENANT_ID));
        return actor;
    }

    private static void setLocalActor(Actor actor){
        actorHolder.set(new ActorWrapper(actor));
    }

    private static void setTracerActor(Actor actor){
        Span activeSpan = GlobalTracer.get().activeSpan();
        activeSpan.setBaggageItem(X_ACTOR_ID, actor.getActorId());
        activeSpan.setBaggageItem(X_TENANT_ID, actor.getTenantId());
    }

    private static void resetLocalActor(){
        actorHolder.remove();
    }

    private static void resetTracerActor(){
        Span activeSpan = GlobalTracer.get().activeSpan();
        activeSpan.setBaggageItem(X_ACTOR_ID,null);
        activeSpan.setBaggageItem(X_TENANT_ID,null);
    }

    private static void removeLocalActor(){
        ActorWrapper actorWrapper = actorHolder.get();
        if(Objects.nonNull(actorWrapper)){
            actorWrapper.remove();
        }
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
