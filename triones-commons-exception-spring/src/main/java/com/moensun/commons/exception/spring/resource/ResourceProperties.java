package com.moensun.commons.exception.spring.resource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Deprecated
public class ResourceProperties {
    private ResourceProperties() {
    }

    public static final Map<Locale, Map<String,String>> codeMap = new HashMap<>();
}
