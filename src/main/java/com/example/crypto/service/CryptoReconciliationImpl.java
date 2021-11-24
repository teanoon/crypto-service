package com.example.crypto.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;

@Service
public class CryptoReconciliationImpl implements CryptoReconciliation {

    @Override
    public void reconciliate(String instrument, String interval) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean reconciliate(CandleStick stick, List<Trade> trades) {
        var expected = new CandleStick();
        expected.setTimestamp(stick.getTimestamp());
        trades.forEach(trade -> {
            if (expected.getOpenPrice() == null) {
                expected.setOpenPrice(trade.getPrice());
            }
            expected.setClosePrice(trade.getPrice());
            if (expected.getHighPrice() == null
                    || trade.getPrice().compareTo(expected.getHighPrice()) > 0) {
                expected.setHighPrice(trade.getPrice());
            }
            if (expected.getLowPrice() == null
                    || trade.getPrice().compareTo(expected.getLowPrice()) < 0) {
                expected.setLowPrice(trade.getPrice());
            }
            expected.setVolume(Optional
                .ofNullable(expected.getVolume())
                .orElse(BigDecimal.ZERO)
                .add(trade.getPrice().multiply(trade.getQuantity())));
        });
        return expected.equals(stick);
    }

}
