package com.moensun.commons.exception.spring.resource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import com.moensun.commons.core.spring.context.SpringContextHolder;
import com.moensun.commons.exception.ResourceProperties;

public class ExceptionMessageHandler implements ApplicationContextAware {
    private Locale local = Locale.getDefault();
    private ResourceBundle resourceBundle;
    private ResourceBundle[] resourceBundles;

    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContext applicationContextSnap = SpringContextHolder.getApplicationContext();
        if(Objects.isNull(applicationContextSnap)){
            SpringContextHolder.setApplicationContext(applicationContext);
        }
    }

    public void setMessageSource(MessageSource messageScore) {
        resourceBundle = new MessageSourceResourceBundle(messageScore, this.local);
    }

    public void setResourceBundle(String resourcePath) {
        if (Objects.nonNull(resourcePath)) {
            String resourcePathFormat = resourcePath.replace("classpath:", "");
            resourceBundle = ResourceBundle.getBundle(resourcePathFormat, this.local);
            setPropertiesMap(resourceBundle);
        }
    }

    public void setResourceBundles(String[] resourcePaths) {
        if (Objects.nonNull(resourcePaths) && resourcePaths.length > 0) {
            List<ResourceBundle> resourceBundleList = new ArrayList<>();
            for (String resourcePath : resourcePaths) {
                String resourcePathFormat = resourcePath.replace("classpath:", "");
                ResourceBundle resourceBundleItem = ResourceBundle.getBundle(resourcePathFormat, this.local);
                setPropertiesMap(resourceBundleItem);
                resourceBundleList.add(resourceBundleItem);
            }
            resourceBundles = resourceBundleList.toArray(new ResourceBundle[resourceBundleList.size()]);
        }
    }

    public void setLocal(Locale local) {
        this.local = local;
    }

    public String text(String code, Object... params) {
        String text = ResourceProperties.codeMap.get(code);
        if (StringUtils.hasText(text)) {
            return null;
        }
        return MessageFormat.format(text, params);
    }

    public void setPropertiesMap(ResourceBundle resourceBundle) {
        Enumeration<String> keys = resourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            ResourceProperties.codeMap.put(key, resourceBundle.getString(key));
        }
    }


}
