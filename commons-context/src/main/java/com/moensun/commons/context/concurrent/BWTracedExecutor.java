package com.moensun.commons.context.concurrent;

import com.bwts.commons.core.operator.Operator;
import com.bwts.commons.core.operator.OperatorContextHolder;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

import java.util.Objects;
import java.util.concurrent.Executor;

public class BWTracedExecutor implements Executor {
    private final Executor delegate;
    protected final Tracer tracer;

    public BWTracedExecutor(Tracer tracer, Executor delegate) {
        this.tracer = tracer;
        this.delegate = delegate;
    }

    public BWTracedExecutor(Executor delegate) {
        this.tracer = GlobalTracer.get();
        this.delegate = delegate;
    }


    @Override
    public void execute(Runnable command) {
        if (OperatorContextHolder.hasTracer()) {
            Span span = createSpan("execute");
            try {
                Span activeSpan = Objects.nonNull(span) ? span : tracer.scopeManager().activeSpan();
                delegate.execute(new com.bwts.commons.core.concurrent.BWTracedRunnable(command,tracer, activeSpan));
            } finally {
                if (Objects.nonNull(span)) {
                    span.finish();
                }
            }

        } else {
            Operator operator = OperatorContextHolder.getOperator();
            delegate.execute(new com.bwts.commons.core.concurrent.BWTracedRunnable(command, operator));
        }
    }

    protected Span createSpan(String operationName) {
        if (Objects.isNull(tracer.activeSpan())) {
            return tracer.buildSpan(operationName).start();
        }
        return null;
    }
}