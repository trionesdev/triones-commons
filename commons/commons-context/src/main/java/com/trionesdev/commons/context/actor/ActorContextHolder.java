package com.trionesdev.commons.context.actor;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.MDC;

import java.util.Objects;
import java.util.function.Supplier;

public class ActorContextHolder {

    private ActorContextHolder() {
    }

    /** 本模块写入的 MDC 键；清理时只移除这些，不影响其他组件写入的键。 */
    private static final String[] MDC_KEYS = {
            ActorConstants.MDC_TRACE_ID,
            ActorConstants.MDC_ACTOR_ID,
            ActorConstants.MDC_USER_ID,
            ActorConstants.MDC_TENANT_ID,
            ActorConstants.MDC_MEMBER_ID,
            ActorConstants.MDC_ROLE,
    };

    private static final TransmittableThreadLocal<Actor> actorHolder = new TransmittableThreadLocal<>();
    /**
     * 被临时切换掉的 Actor 栈。使用不可变链表：TTL 向子线程传递的是同一引用，
     * 不可变结构可避免父子线程互相修改。
     */
    private static final TransmittableThreadLocal<Frame> actorStack = new TransmittableThreadLocal<>();

    private record Frame(Actor actor, Frame next) {
    }

    public static void setActor(Actor actor) {
        setLocalActor(actor);
    }

    public static Actor getActor() {
        return actorHolder.get();
    }

    /**
     * 清除当前 Actor、切换栈及本模块写入的 MDC 键。请在请求或任务结束时调用。
     */
    public static void resetActor() {
        actorHolder.remove();
        actorStack.remove();
        removeMdc();
    }

    /**
     * 保存当前 Actor（可以为 null）并切换为 {@code actor}，需与 {@link #removeActorsFirst()} 成对调用。
     */
    public static void addActorsFirst(Actor actor) {
        if (Objects.isNull(actor)) {
            return;
        }
        push(actor);
    }

    private static void push(Actor actor) {
        actorStack.set(new Frame(getActor(), actorStack.get()));
        setLocalActor(actor);
    }

    /**
     * 恢复最近一次 {@link #addActorsFirst(Actor)} 之前的 Actor；栈为空时清除当前 Actor。
     */
    public static void removeActorsFirst() {
        Frame frame = actorStack.get();
        if (frame == null) {
            actorHolder.remove();
            removeMdc();
            return;
        }
        if (frame.next() == null) {
            actorStack.remove();
        } else {
            actorStack.set(frame.next());
        }
        setLocalActor(frame.actor());
    }

    /**
     * 以 {@code actor} 身份执行，结束后（包括抛出异常时）恢复原 Actor。
     */
    public static void runAs(Actor actor, Runnable runnable) {
        runAs(actor, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * 以 {@code actor} 身份执行并返回结果，结束后（包括抛出异常时）恢复原 Actor。
     * {@code actor} 为 null 时以无 Actor 状态执行。
     */
    public static <T> T runAs(Actor actor, Supplier<T> supplier) {
        push(actor);
        try {
            return supplier.get();
        } finally {
            removeActorsFirst();
        }
    }

    private static void setLocalActor(Actor actor) {
        if (actor == null) {
            actorHolder.remove();
            removeMdc();
            return;
        }
        actorHolder.set(actor);
        putMdc(ActorConstants.MDC_TRACE_ID, actor.getTraceId());
        putMdc(ActorConstants.MDC_ACTOR_ID, actor.getActorId());
        putMdc(ActorConstants.MDC_USER_ID, actor.getUserId());
        putMdc(ActorConstants.MDC_TENANT_ID, actor.getTenantId());
        putMdc(ActorConstants.MDC_MEMBER_ID, actor.getMemberId());
        putMdc(ActorConstants.MDC_ROLE, actor.getRole());
    }

    /** 值为 null 时移除，避免切换 Actor 后残留上一个 Actor 的值。 */
    private static void putMdc(String key, String value) {
        if (value == null) {
            MDC.remove(key);
        } else {
            MDC.put(key, value);
        }
    }

    private static void removeMdc() {
        for (String key : MDC_KEYS) {
            MDC.remove(key);
        }
    }

}
