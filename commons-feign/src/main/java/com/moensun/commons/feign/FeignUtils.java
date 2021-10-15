package com.moensun.commons.feign;

import feign.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FeignUtils {
    public static String requestUrl(Response response) {
        return response.request().url();
    }

    public static String requestBody(Response response) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(response.body().asReader(StandardCharsets.UTF_8))) {
            StringBuilder bodyBuffer = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                bodyBuffer.append(line);
            }
            return bodyBuffer.toString();
        }
    }
}
