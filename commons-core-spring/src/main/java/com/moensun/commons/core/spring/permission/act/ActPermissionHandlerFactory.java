package com.moensun.commons.core.spring.permission.act;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.map.MultiKeyMap;

@RequiredArgsConstructor
public class ActPermissionHandlerFactory {
    private final MultiKeyMap<String, AbsActPermissionHandler> actPermissionHandlerMap = new MultiKeyMap<>();
}
