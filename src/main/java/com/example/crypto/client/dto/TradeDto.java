package com.example.crypto.client.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import com.example.crypto.model.Trade;
import com.example.crypto.model.Trade.Side;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeDto {

    private Long d;
    private String i;
    private String s;
    private BigDecimal p;
    private BigDecimal q;
    private Long t;

    public Trade build() {
        var side = s == Side.BUY.name().toLowerCase() ? Side.BUY : Side.SELL;
        return new Trade(d, i, side, p, q, new Date(t));
    }

}
