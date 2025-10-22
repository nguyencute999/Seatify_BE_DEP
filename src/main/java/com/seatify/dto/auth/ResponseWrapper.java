package com.seatify.dto.auth;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public class ResponseWrapper<T> {
    public HttpStatus status;
    public int code;
    public String message;
    public T data;
}


