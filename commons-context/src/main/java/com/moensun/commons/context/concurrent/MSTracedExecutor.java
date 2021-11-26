package com.moensun.commons.context.concurrent;

import com.moensun.commons.context.actor.Actor;
import com.moensun.commons.context.actor.ActorContextHolder;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

import java.util.Objects;
import java.util.concurrent.Executor;

public class MSTracedExecutor implements Executor {
    private final Executor delegate;
    protected final Tracer tracer;

    public MSTracedExecutor(Tracer tracer, Executor delegate) {
        this.tracer = tracer;
        this.delegate = delegate;
    }

    public MSTracedExecutor(Executor delegate) {
        this.tracer = GlobalTracer.get();
        this.delegate = delegate;
    }


    @Override
    public void execute(Runnable command) {
        if (ActorContextHolder.hasTracer()) {
            Span span = createSpan("execute");
            try {
                Span activeSpan = Objects.nonNull(span) ? span : tracer.scopeManager().activeSpan();
                delegate.execute(new MSTracedRunnable(command,tracer, activeSpan));
            } finally {
                if (Objects.nonNull(span)) {
                    span.finish();
                }
            }

        } else {
            Actor actor = ActorContextHolder.getActor();
            delegate.execute(new MSTracedRunnable(command, actor));
        }
    }

    protected Span createSpan(String operationName) {
        if (Objects.isNull(tracer.activeSpan())) {
            return tracer.buildSpan(operationName).start();
        }
        return null;
    }
}