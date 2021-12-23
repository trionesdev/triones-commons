package com.moensun.commons.core.spring.event.act;

public interface ActEventHandler {
    void before(ActEvent actEvent, Object[] args);
    void after(ActEvent actEvent, Object[] args);
    Object aroundBefore(ActEvent actEvent, Object[] args);
    void aroundAfter(ActEvent actEvent, Object[] args, Object previousData, Object result);
    void afterRetuning(ActEvent actEvent, Object[] args, Object result);
    void afterThrowing(ActEvent actEvent, Object[] args, Exception ex);
}
