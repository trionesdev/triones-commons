package com.trionesdev.commons.i18n.locale;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.*;
import java.util.stream.Collectors;

public class LocaleUtils {

    public List<LocaleInfo> getDisplayNamesByLanguageTags(Set<String> languageTags, Locale locale) {
        Set<Locale> locales = languageTags.stream().map(Locale::forLanguageTag).collect(Collectors.toSet());
        return getDisplayNames(locales, locale);
    }

    public List<LocaleInfo> getDisplayNames(Set<Locale> locales, Locale locale) {
        MultiValuedMap<String, Locale> langMultiMap = new HashSetValuedHashMap<>();
        locales.forEach(t -> langMultiMap.put(t.getLanguage(), t));
        return locales.stream().map(t -> {
            Collection<Locale> langLocals = langMultiMap.get(t.getLanguage());
            return LocaleInfo.builder().code(t.toLanguageTag()).language(t.getLanguage()).country(t.getCountry())
                    .displayName(t.getDisplayLanguage(t) + ((Objects.nonNull(langLocals) && langLocals.size() > 1) ? "(" + t.getCountry() + ")" : "")).build();
        }).collect(Collectors.toList());
    }
}
