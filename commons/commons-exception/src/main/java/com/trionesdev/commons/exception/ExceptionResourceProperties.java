package com.trionesdev.commons.exception;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异常消息资源。查找顺序：代码注册的 {@link #codeMap} → 资源文件（{@link #setResourcePaths(String...)}）。
 * <p>
 * 本类的任何方法都不会因资源缺失或消息格式错误而抛出异常，以免掩盖调用方原本要抛出的业务异常。
 */
public class ExceptionResourceProperties {
    private static final System.Logger LOGGER = System.getLogger(ExceptionResourceProperties.class.getName());

    private ExceptionResourceProperties() {
    }

    private static volatile String[] resourcePaths = new String[]{};
    /**
     * 通过代码注册的消息，优先于资源文件。线程安全，但建议在启动阶段写入。
     */
    public static final Map<Locale, Map<String, String>> codeMap = new ConcurrentHashMap<>();
    /** 从资源文件加载的消息缓存，按 Locale 懒加载。 */
    private static final Map<Locale, Map<String, String>> bundleCache = new ConcurrentHashMap<>();

    /**
     * 设置资源文件路径（ResourceBundle 基名，支持 {@code classpath:} 前缀），会清空已加载的资源缓存。
     */
    public static void setResourcePaths(String... resourcePaths) {
        ExceptionResourceProperties.resourcePaths = resourcePaths == null ? new String[]{} : resourcePaths.clone();
        bundleCache.clear();
    }

    public static String text(String code, Object... params) {
        return text(Locale.getDefault(), code, params);
    }

    /**
     * @return 格式化后的消息；未找到时返回 null
     */
    public static String text(Locale locale, String code, Object... params) {
        if (code == null) {
            return null;
        }
        if (Objects.isNull(locale)) {
            locale = Locale.getDefault();
        }
        String text = lookup(codeMap.get(locale), code);
        if (StringUtils.isBlank(text)) {
            text = lookup(bundleCache.computeIfAbsent(locale, ExceptionResourceProperties::getCodeMap), code);
        }
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return format(text, params);
    }

    /**
     * 加载指定语言的资源文件消息。找不到的资源路径会被跳过并记录警告。
     */
    public static Map<String, String> getCodeMap(Locale locale) {
        Map<String, String> map = new HashMap<>();
        for (String resourcePath : resourcePaths) {
            String baseName = resourcePath.replace("classpath:", "");
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
                for (String key : bundle.keySet()) {
                    map.put(key, bundle.getString(key));
                }
            } catch (MissingResourceException e) {
                LOGGER.log(System.Logger.Level.WARNING,
                        "Exception message bundle not found: " + baseName + " (" + locale + ")");
            }
        }
        return map;
    }

    /**
     * 使用 {@link MessageFormat} 格式化；模板非法时返回原文。
     */
    static String format(String pattern, Object... params) {
        if (pattern == null || params == null) {
            return pattern;
        }
        try {
            return MessageFormat.format(pattern, params);
        } catch (IllegalArgumentException e) {
            return pattern;
        }
    }

    private static String lookup(Map<String, String> messages, String code) {
        return messages == null ? null : messages.get(code);
    }
}
