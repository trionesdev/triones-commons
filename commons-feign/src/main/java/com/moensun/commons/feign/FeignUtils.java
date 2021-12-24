package com.moensun.commons.feign;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import feign.Response;

import java.io.IOException;
import java.io.InputStreamReader;

public class FeignUtils {
    public static String requestUrl(Response response) {
        return response.request().url();
    }

    public static String requestBody(Response response) throws IOException {
        return CharStreams.toString(new InputStreamReader(response.body().asInputStream(), Charsets.UTF_8));
    }
}
