package com.moensun.commons.feign.codec;

import feign.Response;
import feign.codec.ErrorDecoder;

public class DefaultErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        return null;
    }
}
