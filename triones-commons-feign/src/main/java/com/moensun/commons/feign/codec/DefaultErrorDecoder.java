package com.moensun.commons.feign.codec;

import com.alibaba.fastjson2.JSON;
import com.moensun.commons.exception.ErrorResponse;
import com.moensun.commons.exception.InternalRequestException;
import com.moensun.commons.feign.FeignUtils;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.moensun.commons.feign.FeignUtils.requestBody;

public class DefaultErrorDecoder implements ErrorDecoder {
    private final Logger logger = LoggerFactory.getLogger(DefaultErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 500) {
            logger.error("INTERNAL_REQUEST_EXCEPTION: url->{},method->{}", FeignUtils.requestUrl(response), methodKey);
            return new InternalRequestException("INTERNAL_REQUEST_EXCEPTION");
        } else if (response.status() == 404) {
            logger.error("URL NOT FOUND : url->{},method->{}", FeignUtils.requestUrl(response), methodKey);
            return new InternalRequestException("INTERNAL_REQUEST_NOT_FOUND");
        } else {
            try {
                ErrorResponse responseError = JSON.parseObject(requestBody(response), ErrorResponse.class);
                return new InternalRequestException(responseError.getCode(), responseError.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new InternalRequestException("INTERNAL_REQUEST_EXCEPTION");
            }
        }
    }
}
