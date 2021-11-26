package com.moensun.commons.context.concurrent;

import com.moensun.commons.context.actor.Actor;
import com.moensun.commons.context.actor.ActorContextHolder;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

import java.util.Objects;
import java.util.concurrent.Callable;

public class MSTracedCallable<V> implements Callable<V> {
    private final Callable<V> delegate;
    private final Tracer tracer;
    private final Span span;
    private final Actor actor;

    public MSTracedCallable(Callable<V> delegate) {
        this(delegate, null, null, null);
    }

    public MSTracedCallable(Callable<V> delegate, Tracer tracer, Span span) {
        this(delegate, tracer, span, null);
    }

    public MSTracedCallable(Callable<V> delegate, Span span) {
        this(delegate, null, span, null);
    }

    public MSTracedCallable(Callable<V> delegate, Actor actor) {
        this(delegate, null, null, actor);
    }

    public MSTracedCallable(Callable<V> delegate, Tracer tracer, Span span, Actor actor) {
        this.delegate = delegate;
        this.tracer = Objects.nonNull(tracer) ? tracer : GlobalTracer.get();
        this.span = Objects.nonNull(span) ? span : Objects.nonNull(this.tracer) ? this.tracer.scopeManager().activeSpan() : null;
        this.actor = Objects.nonNull(actor) ? actor : ActorContextHolder.getActor();
    }

    @Override
    public V call() throws Exception {
        if (ActorContextHolder.hasTracer()) {
            Scope scope = Objects.isNull(span) ? null : tracer.scopeManager().activate(span);
            try {
                return delegate.call();
            } finally {
                if (Objects.nonNull(scope)) {
                    scope.close();
                }
            }
        } else {
            ActorContextHolder.setActor(actor);
            return delegate.call();
        }
    }
}
