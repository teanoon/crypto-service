package com.example.crypto.service;

import reactor.core.publisher.Mono;

import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;

public interface CryptoReconciliation {

    /**
     * Repeatedly reconciliate {@code instrument}'s {@link CandleStick} and {@link Trade}
     * at {@code intervalString} for the last {@code intervalCount}
     *
     * @param instrument the instrument name
     * @param intervalString the interval
     * @param intervalCount the number of intervals
     */
    Mono<Void> reconciliate(String instrument, String intervalString, int intervalCount);

}
