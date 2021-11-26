package com.example.crypto.client.dto;

import java.math.BigDecimal;

import lombok.Data;

import com.example.crypto.model.CandleStick;

@Data
public class CandleStickDto {

    private BigDecimal o;
    private BigDecimal c;
    private BigDecimal h;
    private BigDecimal l;
    private BigDecimal v;
    private Long t;

    public CandleStick build() {
        return new CandleStick(t, o, c, h, l, v);
    }

}
