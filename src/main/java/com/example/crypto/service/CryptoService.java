package com.example.crypto.service;

import reactor.core.publisher.Flux;

import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;

public interface CryptoService {

    Flux<CandleStick> getCandleSticks(String instrument, String interval);

    Flux<Trade> getTrades(String instrument);

}
