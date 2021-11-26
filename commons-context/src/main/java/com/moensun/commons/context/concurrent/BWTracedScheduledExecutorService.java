package com.moensun.commons.context.concurrent;

import com.bwts.commons.core.operator.Operator;
import com.bwts.commons.core.operator.OperatorContextHolder;
import io.opentracing.Span;
import io.opentracing.Tracer;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BWTracedScheduledExecutorService extends com.bwts.commons.core.concurrent.BWTracedExecutorService implements ScheduledExecutorService {
    private final ScheduledExecutorService delegate;

    public BWTracedScheduledExecutorService(ScheduledExecutorService delegate, Tracer tracer) {
        super(delegate, tracer);
        this.delegate = delegate;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        Span span = createSpan("schedule");
        try {
            Span toActivate = span != null ? span : tracer.activeSpan();
            Operator operator = OperatorContextHolder.getOperator();
            return delegate.schedule(new com.bwts.commons.core.concurrent.BWTracedRunnable(runnable, tracer, toActivate, operator), delay, timeUnit
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
            Operator operator = OperatorContextHolder.getOperator();
            return delegate.schedule(new com.bwts.commons.core.concurrent.BWTracedCallable<V>(callable, tracer, toActivate, operator), delay, timeUnit);
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
            Operator operator = OperatorContextHolder.getOperator();
            return delegate.scheduleAtFixedRate(new com.bwts.commons.core.concurrent.BWTracedRunnable(runnable, tracer, toActivate, operator), initialDelay, period, timeUnit);
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
            Operator operator = OperatorContextHolder.getOperator();
            return delegate.scheduleWithFixedDelay(new com.bwts.commons.core.concurrent.BWTracedRunnable(runnable, tracer, toActivate,operator), initialDelay, delay, timeUnit);
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }
}
