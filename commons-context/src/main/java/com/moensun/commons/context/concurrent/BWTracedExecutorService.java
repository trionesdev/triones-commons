package com.moensun.commons.context.concurrent;

import com.bwts.commons.core.operator.Operator;
import com.bwts.commons.core.operator.OperatorContextHolder;
import io.opentracing.Span;
import io.opentracing.Tracer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class BWTracedExecutorService extends BWTracedExecutor implements ExecutorService {
    private final ExecutorService delegate;
    protected final Tracer tracer;


    public BWTracedExecutorService(ExecutorService delegate, Tracer tracer) {
        super(tracer, delegate);
        this.tracer = tracer;
        this.delegate = delegate;
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        Span span = createSpan("submit");
        try {
            Span toActivate = span != null ? span : tracer.scopeManager().activeSpan();
            Operator operator = OperatorContextHolder.getOperator();
            return delegate.submit(new BWTracedCallable<T>(callable, tracer, toActivate, operator));
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T result) {
        Span span = createSpan("submit");
        try {
            Span toActivate = span != null ? span : tracer.scopeManager().activeSpan();
            Operator operator = OperatorContextHolder.getOperator();
            return delegate.submit(new BWTracedRunnable(runnable, tracer, toActivate, operator), result);
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        Span span = createSpan("submit");
        try {
            Span toActivate = span != null ? span : tracer.scopeManager().activeSpan();
            Operator operator = OperatorContextHolder.getOperator();
            return delegate.submit(new BWTracedRunnable(runnable, tracer, toActivate, operator));
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
        Span span = createSpan("invokeAll");
        try {
            Span toActivate = span != null ? span : tracer.scopeManager().activeSpan();
            return delegate.invokeAll(toTraced(collection, toActivate));
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long timeout, TimeUnit unit) throws InterruptedException {
        Span span = createSpan("invokeAll");
        try {
            Span toActivate = span != null ? span : tracer.scopeManager().activeSpan();
            return delegate.invokeAll(toTraced(collection, toActivate), timeout, unit);
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        Span span = createSpan("invokeAny");
        try {
            Span toActivate = span != null ? span : tracer.scopeManager().activeSpan();
            return delegate.invokeAny(toTraced(collection, toActivate));
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        Span span = createSpan("invokeAny");
        try {
            Span toActivate = span != null ? span : tracer.scopeManager().activeSpan();
            return delegate.invokeAny(toTraced(collection, toActivate), l, timeUnit);
        } finally {
            if (span != null) {
                span.finish();
            }
        }
    }

    private <T> Collection<? extends Callable<T>> toTraced(Collection<? extends Callable<T>> delegate, Span toActivate) {
        List<Callable<T>> tracedCallables = new ArrayList<Callable<T>>(delegate.size());
        Operator operator = OperatorContextHolder.getOperator();
        for (Callable<T> callable : delegate) {
            tracedCallables.add(new BWTracedCallable<T>(callable, tracer, toActivate, operator));
        }

        return tracedCallables;
    }

}
