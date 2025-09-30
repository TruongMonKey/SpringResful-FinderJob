package com.example.JobFinder.util;

import com.example.JobFinder.domain.response.RestResponse;
import com.example.JobFinder.util.annotation.ApiMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
            MediaType selectedContentType, Class selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {

        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        // Nếu body là String thì phải tự serialize
        if (body instanceof String) {
            RestResponse<Object> res = new RestResponse<>();
            res.setStatusCode(status);
            res.setData(body);

            ApiMessage messageAnn = returnType.getMethodAnnotation(ApiMessage.class);
            res.setMessage(messageAnn != null ? messageAnn.value() : "Call API Success");

            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(res);
            } catch (Exception e) {
                throw new RuntimeException("Error converting response to JSON", e);
            }
        }

        // Nếu lỗi (status >= 400) thì trả body gốc (ExceptionHandler xử lý riêng)
        if (status >= 400) {
            return body;
        }

        if (body instanceof String || body instanceof Resource) {
            return body;
        }

        // Nếu ok thì wrap vào RestResponse
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(status);
        res.setData(body);

        ApiMessage messageAnn = returnType.getMethodAnnotation(ApiMessage.class);
        res.setMessage(messageAnn != null ? messageAnn.value() : "Call API Success");

        return res;
    }
}
