package com.moensun.commons.opentracing.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;

public class TllThreadLocalScopeManager implements ScopeManager {
    final TransmittableThreadLocal<TllThreadLocalScope> tlsScope = new TransmittableThreadLocal<TllThreadLocalScope>();

    @Override
    public Scope activate(Span span) {
        return new TllThreadLocalScope(this, span);
    }

    @Override
    public Span activeSpan() {
        TllThreadLocalScope scope = tlsScope.get();
        return scope == null ? null : scope.span();
    }
}
