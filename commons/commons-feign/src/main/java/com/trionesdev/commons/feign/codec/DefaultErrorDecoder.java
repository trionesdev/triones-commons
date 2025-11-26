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

import java.util.Objects;

public class DefaultErrorDecoder implements ErrorDecoder {
    private final Logger logger = LoggerFactory.getLogger(DefaultErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        TrionesError error = TrionesError.builder().code("INTERNAL_REQUEST_EXCEPTION").build();
        if (response.status() == 500) {
            logger.error("[INTERNAL_REQUEST_EXCEPTION] url->{},method->{}", FeignUtils.requestUrl(response), methodKey);
            error.setMessage("Internal Request Internal Exception");
            return new InternalRequestException(error);
        } else if (response.status() == 401) {
            String errMsg = String.format("[INTERNAL_REQUEST_EXCEPTION] Not Authentication, url: %s,method: %s", FeignUtils.requestUrl(response), methodKey);
            logger.error(errMsg);
            error.setMessage("Internal Request Not Authentication");
            return new InternalRequestException(error);
        } else if (response.status() == 404) {
            String errMsg = String.format("[INTERNAL_REQUEST_EXCEPTION] Request URL not found, url: %s,method: %s", FeignUtils.requestUrl(response), methodKey);
            logger.error(errMsg);
            error.setMessage("Internal Request Not Found");
            return new InternalRequestException(error);
        } else {
            try {
                ErrorResponse responseError = JSON.parseObject(FeignUtils.requestBody(response), ErrorResponse.class);
                if (Objects.nonNull(responseError)) {
                    String errMsg = String.format("[INTERNAL_REQUEST_EXCEPTION] Http Code:%s，error message:%s", responseError.getCode(), responseError.getMessage());
                    logger.error(errMsg);
                    error.setMessage("Internal Request Unknown Exception");
                }
                return new InternalRequestException(error);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                error.setMessage("Internal Request Unknown Exception");
                throw new InternalRequestException(error);
            }
        }
    }
}
