package com.example.crypto.client.dto;

import lombok.Data;

@Data
public class Response<T> {

    private int code;
    private String method;
    private T result;

}
