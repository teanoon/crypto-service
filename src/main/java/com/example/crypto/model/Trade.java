package com.example.crypto.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    public static enum Side {
        BUY, SELL
    }

    private Long id;
    private String instrument;
    private Side side;
    private BigDecimal price;
    private BigDecimal quantity;
    private Date date;

}
