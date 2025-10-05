package com.example.JobFinder.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import com.example.JobFinder.domain.response.RestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        // Gọi delegate để Spring set mã 401, header WWW-Authenticate,...
        this.delegate.commence(request, response, authException);

        // Chỉ ghi JSON nếu response chưa được commit
        if (!response.isCommitted()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");

            RestResponse<Object> res = new RestResponse<>();
            res.setStatusCode(HttpStatus.UNAUTHORIZED.value());

            // Lấy thông báo lỗi chi tiết (nếu có)
            String errorMessage = Optional.ofNullable(authException.getCause())
                    .map(Throwable::getMessage)
                    .orElse(authException.getMessage());

            // Gộp cả thông tin lỗi cụ thể nếu bạn muốn debug
            res.setMessage("Token không hợp lệ: " + errorMessage);

            mapper.writeValue(response.getWriter(), res);
        }
    }

}
