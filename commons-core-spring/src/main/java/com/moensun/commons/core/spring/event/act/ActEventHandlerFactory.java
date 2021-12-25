package com.moensun.commons.core.spring.event.act;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ActEventHandlerFactory {
    private final MultiKeyMap<String, AbsActEventHandler> actEventHandlersMap = new MultiKeyMap<>();

    private final List<AbsActEventHandler> actEventHandlers;

    @PostConstruct
    public void init() {
        if (CollectionUtils.isNotEmpty(actEventHandlers)) {
            actEventHandlers.forEach(t -> {
                ActEventHandler actEventHandler = AnnotationUtils.findAnnotation(t.getClass(), ActEventHandler.class);
                if (Objects.nonNull(actEventHandler)) {
                    actEventHandlersMap.put( actEventHandler.subject(), actEventHandler.actionMethod(), actEventHandler.action(), t);
                }
            });
        }
    }

    public AbsActEventHandler get(ActEvent actEvent) {
        return actEventHandlersMap.get( actEvent.subject(), actEvent.actionMethod(), actEvent.action());
    }

}
