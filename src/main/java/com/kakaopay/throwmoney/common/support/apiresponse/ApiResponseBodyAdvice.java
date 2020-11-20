package com.kakaopay.throwmoney.common.support.apiresponse;

import com.kakaopay.throwmoney.common.exception.AbstractApiException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice {

    private static final Marker API_ERROR_MARKER = MarkerFactory.getMarker("API_ERROR");

    private String[] includes;

    static AntPathMatcher matcher = new AntPathMatcher(".");

    @Autowired(required = false)
    WebMvcProperties webMvcProperties;

    @Autowired(required = false)
    DispatcherServlet dispatcherServlet;

    public ApiResponseBodyAdvice(String[] includes) {
        this.includes = includes;
    }

    /**
     * 404 not found 용
     * @return ErrorInfo
     */
    @Bean
    public ErrorAttributes notFoundErrorAttributes(){
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
                Map<String, Object> attributes = super.getErrorAttributes(webRequest, includeStackTrace);
                Object error = attributes.get("error");
                if(error == null) {
                    error = attributes.get("message");
                }
                if(error == null) {
                    error = "Not Found";
                }
                Map<String, Object> result = new HashMap<>();
                result.put("error", new ApiErrorResponse(error.toString()).getError());
                return result;
            }
        };
    }

    /**
     * method 반환 타입이 String 이고 ApiResponse가 적용되면 오류 나므로 순서 조정
     */
    @Configuration
    protected static class FixedStringMessageConverter implements WebMvcConfigurer {

        /**
         * StringHttpMessageConverter 를 뒤로 이전 시킴,
         * @param converters 변경할 converter
         */
        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            List<HttpMessageConverter<?>> messageConverters = converters.stream()
                    .sorted((o1, o2) -> {
                        if(o1 instanceof StringHttpMessageConverter) {
                            return 1;
                        }
                        if(o2 instanceof StringHttpMessageConverter) {
                            return -1;
                        }
                        return 0;
                    })
                    .collect(Collectors.toList());
            converters.clear();
            converters.addAll(messageConverters);
        }
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        Class<?> containingClass = returnType.getContainingClass();
        String canonicalName = containingClass.getCanonicalName();

        for (int i = 0; i < includes.length; i++) {
            if (matcher.match(includes[i], canonicalName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest httpRequest, ServerHttpResponse httpResponse) {
        IApiResponse response;
        HttpHeaders headers = httpResponse.getHeaders();

        if (body instanceof IApiResponse) {
            response = (IApiResponse) body;
            HttpStatus httpStatus = response.httpStatus();
            if (!HttpStatus.OK.equals(httpStatus)) {
                try {
                    httpResponse.setStatusCode(httpStatus);
                } catch (Exception e) {
                }
            }
            if(response.headers() != null)
                response.headers().forEach((s, s2) -> headers.addAll(s, s2));
        } else {
            response = new ApiResponse(body);
        }
        //trace id header 추가
        if (!headers.containsKey("X-Trace-Id")) {
            UUID randomUUID = UUID.randomUUID();
            String uuid = randomUUID.toString();
            headers.add("X-Trace-Id", uuid);
        }
        return response;
    }

    /**
     * 공통 exception 처리
     *
     * @param req 요청
     * @param res 응답
     * @param ex  예외
     * @return ApiResponse 응답
     */
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public static ApiErrorResponse defaultErrorHandler(HttpServletRequest req, HttpServletResponse res, Throwable ex) {
        String message = "";
        //unknown code
        int code = Integer.MIN_VALUE;

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        // AbstractApiException 상속받아 구현한 Exception인 경우
        if (ex instanceof AbstractApiException) {
            AbstractApiException apiException = (AbstractApiException) ex;
            httpStatus = apiException.getHttpStatus();
            code = apiException.getCode();
        } else if (ex instanceof NoHandlerFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            ResponseStatus status = ex.getClass().getAnnotation(ResponseStatus.class);
            if (status != null) {
                httpStatus = status.value();
                message = status.reason();
            }
        }
        if (message == null || message.isEmpty()) {
            message = ex.getLocalizedMessage();
        }
        if (message == null || message.isEmpty()) {
            message = ex.getMessage();
        }

//		res.setContentType("text/plain;charset=UTF-8");
//		res.setHeader("Server", _HostName());

        String errorURL = req.getRequestURL().toString();
        UUID randomUUID = UUID.randomUUID();
        String uuid = randomUUID.toString();
        res.addHeader("X-Trace-Id", uuid);
        res.addHeader("X-Error-Url", errorURL);
//		res.addHeader("X-Error-Message", URLEncoder.encode(message, "UTF-8"));
//		res.addIntHeader("X-Error-Code", code);
        //res.addHeader("Exception", ex.getClass().getName());

        String hostName = _HostName();

        // log.error("{} {}", uuid, hostName, hostName, hostName);
        //FIXME LOG
        log.error(API_ERROR_MARKER, "[{}], {}, {}", uuid, hostName, errorURL, ex);

        if (!HttpStatus.OK.equals(httpStatus)) {
            try {
                res.setStatus(httpStatus.value());
            } catch (Exception e) {
            }
        }
        ApiErrorResponse apiResponse = new ApiErrorResponse(message, code, httpStatus);
        return apiResponse;
    }

    private static String _HostName() {
        try {
            String result = InetAddress.getLocalHost().getHostName();
            // To get the Canonical host name
            // String canonicalHostName =
            // InetAddress.getLocalHost().getCanonicalHostName();
            if (result != null && result.trim() == "")
                return result;
        } catch (UnknownHostException e) {
            // failed;
        }

        // try environment properties.
        String host = System.getenv("COMPUTERNAME");
        if (host != null)
            return host;
        host = System.getenv("HOSTNAME");
        if (host != null)
            return host;
        return null;
    }
}
