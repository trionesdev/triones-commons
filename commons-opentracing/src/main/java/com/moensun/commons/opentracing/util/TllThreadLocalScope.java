package com.moensun.commons.opentracing.util;

import io.opentracing.Scope;
import io.opentracing.Span;

public class TllThreadLocalScope implements Scope {
    private final TllThreadLocalScopeManager scopeManager;
    private final Span wrapped;
    private final TllThreadLocalScope toRestore;

    public TllThreadLocalScope(TllThreadLocalScopeManager scopeManager, Span wrapped) {
        this.scopeManager = scopeManager;
        this.wrapped = wrapped;
        this.toRestore = scopeManager.tlsScope.get();
        scopeManager.tlsScope.set(this);
    }

    @Override
    public void close() {
        if (scopeManager.tlsScope.get() != this) {
            // This shouldn't happen if users call methods in the expected order. Bail out.
            return;
        }

        scopeManager.tlsScope.set(toRestore);
    }

    Span span() {
        return wrapped;
    }
}
