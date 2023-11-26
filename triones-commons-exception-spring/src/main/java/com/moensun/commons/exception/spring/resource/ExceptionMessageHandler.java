package com.moensun.commons.exception.spring.resource;

import com.moensun.commons.core.spring.context.SpringContextHolder;
import com.moensun.commons.exception.ExceptionResourceProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;
import java.util.*;

@Deprecated
public class ExceptionMessageHandler implements ApplicationContextAware {

    private String[] resourcePaths;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContext applicationContextSnap = SpringContextHolder.getApplicationContext();
        if (Objects.isNull(applicationContextSnap)) {
            SpringContextHolder.setApplicationContext(applicationContext);
        }
    }

    public void setResourcePaths(String[] resourcePaths) {
        this.resourcePaths = resourcePaths;
        ExceptionResourceProperties.setResourcePaths(resourcePaths);
    }

    public String text(String code, Object... params) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> localeCodeMap = ResourceProperties.codeMap.get(locale);
        if (Objects.isNull(localeCodeMap)) {
            Map<String, String> codeMap = getCodeMap(locale);
            if (Objects.nonNull(codeMap)) {
                ResourceProperties.codeMap.put(locale, codeMap);
                localeCodeMap = codeMap;
            }
        }
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


    public Map<String, String> getCodeMap(Locale locale) {
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
