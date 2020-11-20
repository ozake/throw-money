package com.kakaopay.throwmoney.common.support.apiresponse;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * APi 응답 반환 Entity
 * @param <T> 반환 타입
 */
public class ApiResponse<T> implements IApiResponse, Serializable {
    private static final long serialVersionUID = -2790327574322809168L;

    private T data;

    @Getter
    @Accessors(fluent = true)
    @JsonIgnore
    private transient HttpStatus httpStatus;

    @Getter
    @Accessors(fluent = true)
    @JsonIgnore
    private transient HttpHeaders headers;

    ApiResponse() {
    }

    public ApiResponse(@NotNull T data) {
        this(data, HttpStatus.OK);
    }

    public ApiResponse(@NotNull T data, @NotNull HttpStatus httpStatus) {
        this(data, httpStatus, new HttpHeaders());
    }

    public ApiResponse(@NotNull T data, @NotNull HttpHeaders headers) {
        this(data, HttpStatus.OK, headers);
    }

    public ApiResponse(@NotNull T data, @NotNull HttpStatus httpStatus, @NotNull HttpHeaders headers) {
        this.data = data;
        this.httpStatus = httpStatus;
        this.headers = headers;
    }

    @JsonGetter("data")
    public T getData() {
        return data;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSetter("data")
    void setData(T data) {
        this.data = data;
    }

}
