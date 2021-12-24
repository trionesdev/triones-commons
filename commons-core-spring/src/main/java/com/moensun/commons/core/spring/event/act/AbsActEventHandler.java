package com.moensun.commons.core.spring.event.act;

public abstract class AbsActEventHandler {
    public void before(ActEvent actEvent, Object[] args) {
    }

    public void after(ActEvent actEvent, Object[] args) {
    }

    public Object aroundBefore(ActEvent actEvent, Object[] args) {
        return null;
    }

    public void aroundAfter(ActEvent actEvent, Object[] args, Object previousData, Object result) {
    }

    public void afterRetuning(ActEvent actEvent, Object[] args, Object result) {
    }

    public void afterThrowing(ActEvent actEvent, Object[] args, Exception ex) {
    }
}
