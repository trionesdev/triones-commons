package com.trionesdev.commons.i18n.locale;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LocaleUtilsTest {

    private static Map<String, String> names(List<LocaleInfo> infos) {
        return infos.stream().collect(Collectors.toMap(LocaleInfo::getCode, LocaleInfo::getDisplayName));
    }

    @Test
    void displaysInRequestedLocale() {
        Map<String, String> names = names(LocaleUtils.getDisplayNamesByLanguageTags(
                Set.of("en-US", "ja-JP"), Locale.SIMPLIFIED_CHINESE));
        assertEquals("英语", names.get("en-US"));
        assertEquals("日语", names.get("ja-JP"));
    }

    @Test
    void nullDisplayLocaleUsesNativeNames() {
        Map<String, String> names = names(LocaleUtils.getDisplayNamesByLanguageTags(
                Set.of("en-US", "zh-CN"), null));
        assertEquals("English", names.get("en-US"));
        assertEquals("中文", names.get("zh-CN"));
    }

    @Test
    void sameLanguageAppendsRegion() {
        Map<String, String> names = names(LocaleUtils.getDisplayNamesByLanguageTags(
                Set.of("zh-CN", "zh-TW", "en-US"), Locale.SIMPLIFIED_CHINESE));
        assertEquals("中文(中国)", names.get("zh-CN"));
        assertEquals("中文(台湾)", names.get("zh-TW"));
        assertEquals("英语", names.get("en-US"));
    }

    @Test
    void sortedByCodeAndEmptyInputSafe() {
        List<LocaleInfo> infos = LocaleUtils.getDisplayNamesByLanguageTags(
                Set.of("zh-CN", "en-US", "ja-JP"), Locale.ENGLISH);
        assertEquals(List.of("en-US", "ja-JP", "zh-CN"), infos.stream().map(LocaleInfo::getCode).toList());
        assertTrue(LocaleUtils.getDisplayNamesByLanguageTags(Set.of(), Locale.ENGLISH).isEmpty());
        assertTrue(LocaleUtils.getDisplayNames(null, Locale.ENGLISH).isEmpty());
    }
}
