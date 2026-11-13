package com.trionesdev.commons.feign.codec;

import com.alibaba.fastjson2.JSON;
import com.trionesdev.commons.exception.ErrorResponse;
import com.trionesdev.commons.exception.InternalRequestException;
import com.trionesdev.commons.exception.TrionesError;
import com.trionesdev.commons.feign.FeignUtils;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 把内部服务调用的非 2xx 响应转换为 {@link InternalRequestException}。
 * <ul>
 *     <li>响应体是带 {@code code} 的 {@link ErrorResponse} 时（任意状态码），透传下游的 code 和 message。</li>
 *     <li>否则使用 {@link #DEFAULT_CODE}，并按状态码给出默认消息。</li>
 * </ul>
 */
public class DefaultErrorDecoder implements ErrorDecoder {
    public static final String DEFAULT_CODE = "INTERNAL_REQUEST_EXCEPTION";
    private static final int MAX_LOGGED_BODY = 1000;

    private final Logger logger = LoggerFactory.getLogger(DefaultErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String url = FeignUtils.requestUrl(response);
        String body = readBody(response);

        ErrorResponse remote = parseErrorResponse(body);
        if (remote != null && remote.getCode() != null && !remote.getCode().isBlank()) {
            logger.error("[{}] status: {}, code: {}, message: {}, url: {}, method: {}",
                    DEFAULT_CODE, status, remote.getCode(), remote.getMessage(), url, methodKey);
            return new InternalRequestException(TrionesError.builder()
                    .code(remote.getCode())
                    .message(literal(remote.getMessage()))
                    .build());
        }

        logger.error("[{}] status: {}, url: {}, method: {}, body: {}",
                DEFAULT_CODE, status, url, methodKey, abbreviate(body));
        return new InternalRequestException(TrionesError.builder()
                .code(DEFAULT_CODE)
                .message(defaultMessage(status))
                .build());
    }

    private String readBody(Response response) {
        try {
            return FeignUtils.requestBody(response);
        } catch (Exception e) {
            logger.warn("[{}] failed to read response body", DEFAULT_CODE, e);
            return null;
        }
    }

    private static ErrorResponse parseErrorResponse(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return JSON.parseObject(body, ErrorResponse.class);
        } catch (Exception e) {
            // 非 JSON（例如网关返回的 HTML 错误页）。
            return null;
        }
    }

    private static String defaultMessage(int status) {
        switch (status) {
            case 401:
                return "Internal Request Not Authentication";
            case 404:
                return "Internal Request Not Found";
            case 500:
                return "Internal Request Internal Exception";
            default:
                return "Internal Request Unknown Exception";
        }
    }

    /**
     * 异常消息会经过 {@link java.text.MessageFormat} 处理，用引号包裹使下游原文（含 {@code '}、{@code {}}）原样保留。
     */
    private static String literal(String message) {
        if (message == null || message.isEmpty()) {
            return null;
        }
        return "'" + message.replace("'", "''") + "'";
    }

    private static String abbreviate(String body) {
        if (body == null || body.length() <= MAX_LOGGED_BODY) {
            return body;
        }
        return body.substring(0, MAX_LOGGED_BODY) + "...";
    }
}
