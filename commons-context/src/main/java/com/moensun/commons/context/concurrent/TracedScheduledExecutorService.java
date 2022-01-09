package com.moensun.commons.context.concurrent;

import com.moensun.commons.context.actor.Actor;
import com.moensun.commons.context.actor.ActorContextHolder;
import io.opentracing.Span;
import io.opentracing.Tracer;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TracedScheduledExecutorService extends TracedExecutorService implements ScheduledExecutorService {
    private final ScheduledExecutorService delegate;

    public TracedScheduledExecutorService(ScheduledExecutorService delegate, Tracer tracer) {
        super(delegate, tracer);
        this.delegate = delegate;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        Span span = createSpan("schedule");
        try {
            Span toActivate = span != null ? span : tracer.activeSpan();
            Actor actor = ActorContextHolder.getActor();
            return delegate.schedule(new TracedRunnable(runnable, tracer, toActivate, actor), delay, timeUnit
            );
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit timeUnit) {
        Span span = createSpan("schedule");
        try {
            Span toActivate = span != null ? span : tracer.activeSpan();
            Actor actor = ActorContextHolder.getActor();
            return delegate.schedule(new TracedCallable<V>(callable, tracer, toActivate, actor), delay, timeUnit);
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit timeUnit) {
        Span span = createSpan("scheduleAtFixedRate");
        try {
            Span toActivate = span != null ? span : tracer.activeSpan();
            Actor actor = ActorContextHolder.getActor();
            return delegate.scheduleAtFixedRate(new TracedRunnable(runnable, tracer, toActivate, actor), initialDelay, period, timeUnit);
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        Span span = createSpan("scheduleWithFixedDelay");
        try {
            Span toActivate = span != null ? span : tracer.activeSpan();
            Actor actor = ActorContextHolder.getActor();
            return delegate.scheduleWithFixedDelay(new TracedRunnable(runnable, tracer, toActivate,actor), initialDelay, delay, timeUnit);
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }
}
