package com.kakaopay.throwmoney.common.support.apiresponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * APi 응답 반환 Entity
 */
public class ApiErrorResponse implements IApiResponse, Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorInfo error;

    @Getter
    @Accessors(fluent = true)
    private transient final HttpStatus httpStatus;

    @Getter
    @Accessors(fluent = true)
    private transient final HttpHeaders headers;


    public ApiErrorResponse(Throwable throwable) {
        this(throwable, HttpStatus.OK);
    }

    public ApiErrorResponse(Throwable throwable, HttpStatus httpStatus) {
        this(throwable, httpStatus, new HttpHeaders());
    }

    public ApiErrorResponse(Throwable throwable, HttpHeaders headers) {
        this(throwable, HttpStatus.OK, headers);
    }

    public ApiErrorResponse(@NotNull Throwable throwable, @NotNull HttpStatus httpStatus, @NotNull HttpHeaders headers) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        String message = throwable.getLocalizedMessage();
        if (message == null || message.isEmpty())
        {
            message = throwable.getMessage();
        }
        if(message != null && !message.isEmpty())
        {
            try {
                message = URLEncoder.encode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
        }
        this.error = new ErrorInfo(message, Integer.MIN_VALUE);
    }

    public ApiErrorResponse(String message) {
        this(message, Integer.MIN_VALUE);
    }

    public ApiErrorResponse(String message, int code) {
        this(message, code, new HttpHeaders());
    }

    public ApiErrorResponse(String message, int code, HttpStatus httpStatus) {
        this(message, code, httpStatus, new HttpHeaders());
    }

    public ApiErrorResponse(String message, int code, HttpHeaders headers) {
        this(message, code, HttpStatus.INTERNAL_SERVER_ERROR, headers);
    }

    public ApiErrorResponse(@NotNull String message, int code, @NotNull HttpStatus httpStatus, @NotNull HttpHeaders headers) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.error = new ErrorInfo(message, code);
    }

    public ErrorInfo getError(){
        return this.error;
    }

    @Value
    static class ErrorInfo {
        private String message;

        private int code;
    }
}
