package com.trionesdev.commons.feign.codec;

import com.trionesdev.commons.exception.InternalRequestException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DefaultErrorDecoderTest {
    private final DefaultErrorDecoder decoder = new DefaultErrorDecoder();

    private static Response response(int status, String body) {
        Request request = Request.create(Request.HttpMethod.GET, "http://user-service/users/1",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
        Response.Builder builder = Response.builder().status(status).reason("r").request(request)
                .headers(Collections.emptyMap());
        if (body != null) {
            builder.body(body, StandardCharsets.UTF_8);
        }
        return builder.build();
    }

    private InternalRequestException decode(int status, String body) {
        Exception ex = decoder.decode("UserClient#get(Long)", response(status, body));
        return assertInstanceOf(InternalRequestException.class, ex);
    }

    @Test
    void propagatesDownstreamErrorCodeAndMessage() {
        InternalRequestException ex = decode(400, "{\"code\":\"USER_LOCKED\",\"message\":\"user {0} isn't active\"}");
        assertEquals("USER_LOCKED", ex.getCode());
        assertEquals("user {0} isn't active", ex.getMessage());
    }

    @Test
    void propagatesDownstreamErrorOn500() {
        InternalRequestException ex = decode(500, "{\"code\":\"ORDER_NOT_PAID\",\"message\":\"订单未支付\"}");
        assertEquals("ORDER_NOT_PAID", ex.getCode());
        assertEquals("订单未支付", ex.getMessage());
    }

    @Test
    void fallsBackToStatusMessages() {
        assertEquals("Internal Request Not Found", decode(404, null).getMessage());
        assertEquals("Internal Request Not Authentication", decode(401, "").getMessage());
        assertEquals("Internal Request Internal Exception", decode(500, "<html>oops</html>").getMessage());
        InternalRequestException other = decode(403, "{\"message\":\"no code\"}");
        assertEquals(DefaultErrorDecoder.DEFAULT_CODE, other.getCode());
        assertEquals("Internal Request Unknown Exception", other.getMessage());
    }
}
