package com.moensun.commons.exception;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ExceptionResourceProperties {
    private ExceptionResourceProperties() {
    }

    public static final Map<Locale, Map<String, String>> codeMap = new HashMap<>();

    public static String text(String code, Object... params) {
        return text(Locale.getDefault(), code, params);
    }

    public static String text(Locale locale, String code, Object... params) {
        if (Objects.isNull(locale)) {
            locale = Locale.getDefault();
        }
        Map<String, String> localeCodeMap = ExceptionResourceProperties.codeMap.get(locale);
        if (Objects.isNull(localeCodeMap)) {
            return null;
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
}
