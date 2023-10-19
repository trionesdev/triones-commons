package com.moensun.commons.security.spring.jwt;

import com.alibaba.fastjson2.JSON;
import com.moensun.commons.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if(log.isWarnEnabled()){
            log.warn(authException.getMessage());
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ErrorResponse errorResponse = ErrorResponse.builder().code(HttpStatus.UNAUTHORIZED.name()).message("未认证").build();
        response.setContentType(APPLICATION_JSON_VALUE+";charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(errorResponse));
    }
}
