package com.example.crypto.service;

import java.util.List;

import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;

public interface CryptoReconciliation {

    void reconciliate(String instrument, String interval);

    boolean reconciliate(CandleStick stick, List<Trade> trades);

}
