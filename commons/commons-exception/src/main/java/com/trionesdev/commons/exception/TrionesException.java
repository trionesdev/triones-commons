package com.trionesdev.commons.exception;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Locale;

public class TrionesException extends RuntimeException {
    private static final long serialVersionUID = -5195341167826040681L;
    private static final Object[] NO_ARGS = new Object[0];

    @Getter
    private String code;
    private String message;
    /** 消息参数值，供异常处理器按请求语言重新解析消息。 */
    private transient Object[] arguments = NO_ARGS;

    @Override
    public String getMessage() {
        return message != null ? message : super.getMessage();
    }

    public TrionesException() {
        super("triones exception");
    }

    public TrionesException(String code, ArgumentValue... params) {
        this(TrionesError.builder().code(code).arguments(params).build());
    }

    public TrionesException(String code, String defaultMessage, ArgumentValue... params) {
        this(TrionesError.builder().code(code).arguments(params).message(defaultMessage).build());
    }

    public TrionesException(Throwable cause, String code, ArgumentValue... params) {
        this(TrionesError.builder().cause(cause).code(code).arguments(params).build());
    }

    public TrionesException(TrionesError error) {
        super(error.getMessage(), error.getCause());
        this.code = error.getCode();
        if (ArrayUtils.isNotEmpty(error.getArguments())) {
            this.arguments = Arrays.stream(error.getArguments())
                    .map(arg -> arg == null ? null : arg.getValue()).toArray();
        }
        this.message = resolveMessage(Locale.getDefault(), error.getMessage());
    }

    /**
     * 消息参数值的副本。
     */
    public Object[] getArguments() {
        return arguments == null ? NO_ARGS : arguments.clone();
    }

    /**
     * 按指定语言重新解析消息，例如在全局异常处理器中按请求语言返回。未找到对应资源时返回 {@link #getMessage()}。
     */
    public String getMessage(Locale locale) {
        String text = ExceptionResourceProperties.text(locale, code, getArguments());
        return StringUtils.isNotBlank(text) ? text : getMessage();
    }

    /**
     * 消息解析失败不能掩盖本异常，因此这里吞掉解析过程中的任何运行时异常。
     */
    private String resolveMessage(Locale locale, String defaultMessage) {
        String text = null;
        try {
            text = ExceptionResourceProperties.text(locale, code, getArguments());
        } catch (RuntimeException ignored) {
            // 资源加载失败时退回默认消息。
        }
        if (StringUtils.isBlank(text) && StringUtils.isNotBlank(defaultMessage)) {
            text = ExceptionResourceProperties.format(defaultMessage, getArguments());
        }
        return text;
    }
}
