package com.example.crypto.dto;

import lombok.Data;

@Data
public class Response<T> {

    private int code;
    private String method;
    private T result;

}
