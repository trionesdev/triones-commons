package com.trionesdev.commons.i18n.locale;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocaleUtils {

    /**
     * @see #getDisplayNames(Set, Locale)
     */
    public static List<LocaleInfo> getDisplayNamesByLanguageTags(Set<String> languageTags, Locale displayLocale) {
        if (languageTags == null || languageTags.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Locale> locales = languageTags.stream().map(Locale::forLanguageTag).collect(Collectors.toSet());
        return getDisplayNames(locales, displayLocale);
    }

    /**
     * 生成语言列表的显示名称，按 {@code code} 排序。
     *
     * @param locales       要展示的语言
     * @param displayLocale 用哪种语言显示名称；为 null 时每个语言用其自身显示（如 English、中文），适合语言切换菜单
     */
    public static List<LocaleInfo> getDisplayNames(Set<Locale> locales, Locale displayLocale) {
        if (locales == null || locales.isEmpty()) {
            return Collections.emptyList();
        }
        // 同一语言出现多次时，需要追加国家/地区以示区分。
        Map<String, Long> languageCounts = locales.stream()
                .collect(Collectors.groupingBy(Locale::getLanguage, Collectors.counting()));
        Function<Locale, Locale> displayIn = locale -> displayLocale != null ? displayLocale : locale;
        return locales.stream()
                .map(locale -> {
                    Locale in = displayIn.apply(locale);
                    String displayName = locale.getDisplayLanguage(in);
                    if (languageCounts.get(locale.getLanguage()) > 1 && !locale.getCountry().isEmpty()) {
                        displayName += "(" + locale.getDisplayCountry(in) + ")";
                    }
                    return LocaleInfo.builder()
                            .code(locale.toLanguageTag())
                            .language(locale.getLanguage())
                            .country(locale.getCountry())
                            .displayName(displayName)
                            .build();
                })
                .sorted(Comparator.comparing(LocaleInfo::getCode))
                .collect(Collectors.toList());
    }
}
