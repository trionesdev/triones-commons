package com.trionesdev.commons.context.actor;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Supplier;

public class ActorContextHolder {
    private static final Logger logger = LoggerFactory.getLogger(ActorContextHolder.class);

    private ActorContextHolder() {
    }

    private static final TransmittableThreadLocal<Actor> actorHolder = new TransmittableThreadLocal<>();
    private static final TransmittableThreadLocal<LinkedBlockingDeque<Actor>> actorQueue = new TransmittableThreadLocal<>();

    public static void setActor(Actor actor) {
        setLocalActor(actor);
    }

    public static Actor getActor() {
        return getLocalActor();
    }

    public static void resetActor() {
        resetLocalActor();
    }

    public static void addActorsFirst(Actor actor) {
        if (Objects.isNull(actor)) {
            return;
        }
        LinkedBlockingDeque<Actor> actors = actorQueue.get();
        if (Objects.isNull(actors)) {
            actors = new LinkedBlockingDeque<>();
            actorQueue.set(actors);
        }
        Actor previousActor = getActor();
        actors.addFirst(previousActor);
        setActor(actor);
    }

    public static void removeActorsFirst() {
        LinkedBlockingDeque<Actor> actors = actorQueue.get();
        if (Objects.isNull(actors) || actors.isEmpty()) {
            setActor(null);
            return;
        }
        try {
            Actor previousActor = actors.removeFirst();
            setActor(previousActor);
        } catch (NoSuchElementException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("[ActorContextHolder] context actors is empty ");
            }
            setActor(null);
        }
    }

    public static void runAs(Actor actor, Runnable runnable) {
        addActorsFirst(actor);
        runnable.run();
        removeActorsFirst();
    }

    public static <T> T runAs(Actor actor, Supplier<T> supplier) {
        addActorsFirst(actor);
        T result = supplier.get();
        removeActorsFirst();
        return result;
    }

    private static Actor getLocalActor() {
        return actorHolder.get();
    }

    private static void setLocalActor(Actor actor) {
        actorHolder.set(actor);
        putLocalMDCContext(actor);
    }

    private static void resetLocalActor() {
        actorHolder.remove();
        cleanLocalMDCContext();
    }

    private static void putLocalMDCContext(Actor actor) {
        if (Objects.nonNull(actor)) {
            MDC.put(ActorConstants.MDC_TRACE_ID, actor.getTraceId());
            MDC.put(ActorConstants.MDC_ACTOR_ID, actor.getActorId());
            MDC.put(ActorConstants.MDC_USER_ID, actor.getUserId());
            MDC.put(ActorConstants.MDC_TENANT_ID, actor.getTenantId());
            MDC.put(ActorConstants.MDC_MEMBER_ID, actor.getMemberId());
            MDC.put(ActorConstants.MDC_ROLE, actor.getRole());
        }
    }

    private static void cleanLocalMDCContext() {
//        MDC.put(ActorConstants.MDC_TRACE_ID, null);
//        MDC.put(ActorConstants.MDC_ACTOR_ID, null);
//        MDC.put(ActorConstants.MDC_USER_ID, null);
//        MDC.put(ActorConstants.MDC_TENANT_ID, null);
//        MDC.put(ActorConstants.MDC_MEMBER_ID, null);
//        MDC.put(ActorConstants.MDC_ROLE, null);
        MDC.clear();
    }

}
