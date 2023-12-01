package com.trionesdev.commons.exception.spring.resource;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

@Deprecated
public class MessageSourceResourceBundle extends ResourceBundle {
    private final MessageSource messageSource;

    private final Locale locale;

    public MessageSourceResourceBundle(MessageSource messageSource, Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale;
    }

    @Override protected Object handleGetObject(String key) {
        try {
            return this.messageSource.getMessage(key, null, this.locale);
        } catch (NoSuchMessageException ex) {
            return null;
        }
    }

    @Override public Enumeration<String> getKeys() {
        throw new UnsupportedOperationException("MessageSourceResourceBundle does not support enumerating its keys");
    }
}
