package com.trionesdev.commons.exception;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExTest {
    private Locale originalLocale;

    @BeforeEach
    void setUp() {
        originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
    }

    @AfterEach
    void tearDown() {
        ExceptionResourceProperties.codeMap.clear();
        ExceptionResourceProperties.setResourcePaths();
        Locale.setDefault(originalLocale);
    }

    @Test
    void codeRegisteredMessage() {
        ExceptionResourceProperties.codeMap.put(Locale.getDefault(), Map.of("TEST", "ce shi"));
        TrionesException ex = new TrionesException("TEST");
        assertEquals("TEST", ex.getCode());
        assertEquals("ce shi", ex.getMessage());
    }

    @Test
    void codeRegisteredMessageWinsOverDefault() {
        ExceptionResourceProperties.codeMap.put(Locale.getDefault(), Map.of("TEST", "ce shi"));
        assertEquals("ce shi", new TrionesException("TEST", "test").getMessage());
    }

    @Test
    void resourceBundleMessage() {
        ExceptionResourceProperties.setResourcePaths("classpath:i18n/error");
        assertEquals("用户不存在", new NotFoundException("USER_NOT_FOUND").getMessage());
    }

    @Test
    void defaultMessageWithArguments() {
        BusinessException ex = new BusinessException("SYSTEM_ERROR", "测试 {0} {1}", ArgumentValue.of("111", "222"));
        assertEquals("测试 111 222", ex.getMessage());
        assertArrayEquals(new Object[]{"111", "222"}, ex.getArguments());
    }

    @Test
    void missingBundleDoesNotHideOriginalException() {
        ExceptionResourceProperties.setResourcePaths("i18n/does-not-exist");
        BusinessException ex = assertDoesNotThrow(() -> new BusinessException("SYSTEM_ERROR", "系统错误"));
        assertEquals("系统错误", ex.getMessage());
    }

    @Test
    void invalidPatternFallsBackToRawText() {
        BusinessException ex = new BusinessException("SYSTEM_ERROR", "bad {x} pattern", ArgumentValue.of("1"));
        assertEquals("bad {x} pattern", ex.getMessage());
    }

    @Test
    void resolveMessageForRequestLocale() {
        ExceptionResourceProperties.setResourcePaths("i18n/error");
        BusinessException ex = new BusinessException("USER_LOCKED", ArgumentValue.of("alice"));
        assertEquals("用户 alice 已被锁定", ex.getMessage());
        assertEquals("User alice is locked", ex.getMessage(Locale.ENGLISH));
    }

    @Test
    void setResourcePathsRefreshesCache() {
        assertNull(new NotFoundException("USER_NOT_FOUND").getMessage());
        ExceptionResourceProperties.setResourcePaths("i18n/error");
        assertEquals("用户不存在", new NotFoundException("USER_NOT_FOUND").getMessage());
    }

    @Test
    void noArgConstructorKeepsMessage() {
        assertEquals("triones exception", new TrionesException().getMessage());
    }
}
