package com.moensun.commons.context.concurrent;

import com.bwts.commons.core.operator.Operator;
import com.bwts.commons.core.operator.OperatorContextHolder;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

import java.util.Objects;

public class BWTracedRunnable implements Runnable {
    private final Runnable delegate;
    private final Tracer tracer;
    private final Span span;
    private final Operator operator;

    public BWTracedRunnable(Runnable delegate) {
        this(delegate, null, null, null);
    }

    public BWTracedRunnable(Runnable delegate, Tracer tracer, Span span) {
        this(delegate, tracer, span, null);
    }

    public BWTracedRunnable(Runnable delegate, Span span) {
        this(delegate, null, span, null);
    }

    public BWTracedRunnable(Runnable delegate, Operator operator) {
        this(delegate, null, null, operator);
    }

    public BWTracedRunnable(Runnable delegate, Tracer tracer, Span span, Operator operator) {
        this.delegate = delegate;
        this.tracer = Objects.nonNull(tracer) ? tracer : GlobalTracer.get();
        this.span = Objects.nonNull(span) ? span : Objects.nonNull(this.tracer) ? this.tracer.scopeManager().activeSpan() : null;
        this.operator = Objects.nonNull(operator) ? operator : OperatorContextHolder.getOperator();
    }

    @Override
    public void run() {
        if (OperatorContextHolder.hasTracer()) {
            Scope scope = Objects.isNull(span) ? null : tracer.scopeManager().activate(span);
            try {
                delegate.run();
            } finally {
                if (Objects.nonNull(scope)) {
                    scope.close();
                }
            }
        } else {
            OperatorContextHolder.setOperator(operator);
            delegate.run();
        }

    }
}
