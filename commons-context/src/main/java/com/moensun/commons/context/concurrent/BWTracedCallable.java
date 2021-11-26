package com.moensun.commons.context.concurrent;

import com.bwts.commons.core.operator.Operator;
import com.bwts.commons.core.operator.OperatorContextHolder;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

import java.util.Objects;
import java.util.concurrent.Callable;

public class BWTracedCallable<V> implements Callable<V> {
    private final Callable<V> delegate;
    private final Tracer tracer;
    private final Span span;
    private final Operator operator;

    public BWTracedCallable(Callable<V> delegate) {
        this(delegate, null, null, null);
    }

    public BWTracedCallable(Callable<V> delegate, Tracer tracer, Span span) {
        this(delegate, tracer, span, null);
    }

    public BWTracedCallable(Callable<V> delegate, Span span) {
        this(delegate, null, span, null);
    }

    public BWTracedCallable(Callable<V> delegate, Operator operator) {
        this(delegate, null, null, operator);
    }

    public BWTracedCallable(Callable<V> delegate, Tracer tracer, Span span, Operator operator) {
        this.delegate = delegate;
        this.tracer = Objects.nonNull(tracer) ? tracer : GlobalTracer.get();
        this.span = Objects.nonNull(span) ? span : Objects.nonNull(this.tracer) ? this.tracer.scopeManager().activeSpan() : null;
        this.operator = Objects.nonNull(operator) ? operator : OperatorContextHolder.getOperator();
    }

    @Override
    public V call() throws Exception {
        if (OperatorContextHolder.hasTracer()) {
            Scope scope = Objects.isNull(span) ? null : tracer.scopeManager().activate(span);
            try {
                return delegate.call();
            } finally {
                if (Objects.nonNull(scope)) {
                    scope.close();
                }
            }
        } else {
            OperatorContextHolder.setOperator(operator);
            return delegate.call();
        }
    }
}
