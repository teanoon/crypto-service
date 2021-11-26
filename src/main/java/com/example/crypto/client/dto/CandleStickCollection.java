package com.example.crypto.client.dto;

import java.util.List;

import lombok.Data;

@Data
public class CandleStickCollection {

    private String instrumentName;
    private String interval;
    private List<CandleStickDto> data;

}
