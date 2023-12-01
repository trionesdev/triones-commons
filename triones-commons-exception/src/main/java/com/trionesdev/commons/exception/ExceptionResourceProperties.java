package com.trionesdev.commons.exception;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.*;


public class ExceptionResourceProperties {

    private ExceptionResourceProperties() {
    }

    private static String[] resourcePaths = new String[]{};
    public static final Map<Locale, Map<String, String>> codeMap = new HashMap<>();

    public static void setResourcePaths(String... resourcePaths) {
        ExceptionResourceProperties.resourcePaths = resourcePaths;
    }
    public static String text(String code, Object... params) {
        return text(Locale.getDefault(), code, params);
    }

    public static String text(Locale locale, String code, Object... params) {
        if (Objects.isNull(locale)) {
            locale = Locale.getDefault();
        }
        Map<String, String> localeCodeMap = ExceptionResourceProperties.codeMap.get(locale);
        if (Objects.isNull(localeCodeMap)) {
            localeCodeMap = getCodeMap(locale);
            if (Objects.nonNull(localeCodeMap)) {
                codeMap.put(locale, localeCodeMap);
            }
        }
        String text = localeCodeMap.get(code);
        if (StringUtils.isBlank(text)) {
            return null;
        }
        if (Objects.isNull(params)) {
            return text;
        }
        return MessageFormat.format(text, params);
    }

    public static Map<String, String> getCodeMap(Locale locale) {
        if (Objects.nonNull(resourcePaths) && resourcePaths.length > 0) {
            Map<String, String> map = new HashMap<>();
            for (String resourcePath : resourcePaths) {
                String resourcePathFormat = resourcePath.replace("classpath:", "");
                ResourceBundle resourceBundleItem = ResourceBundle.getBundle(resourcePathFormat, locale);
                if (Objects.nonNull(resourceBundleItem)) {
                    Enumeration<String> keys = resourceBundleItem.getKeys();
                    while (keys.hasMoreElements()) {
                        String key = keys.nextElement();
                        map.put(key, resourceBundleItem.getString(key));
                    }
                }

            }
            return map;
        }
        return null;
    }
}
