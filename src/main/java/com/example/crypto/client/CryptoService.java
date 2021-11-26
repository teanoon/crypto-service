package com.example.crypto.client;

import reactor.core.publisher.Flux;

import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;

public interface CryptoService {

    /**
     * Retrieves candlesticks (k-line data history) over a given period for an instrument (e.g. BTC_USDT)
     *
     * @param instrument the instrument name, e.g. BTC_USDT, ETH_CRO, etc.
     * @param interval one of 1m 5m 15m 30m 1h 4h 6h 12h 1D 7D 14D 1M
     * @return a list of candle sticks sorting by time ascending
     */
    Flux<CandleStick> getCandleSticks(String instrument, String interval);

    /**
     * Fetches the public trades for a particular instrument recently.
     *
     * @param instrument the instrument name, e.g. BTC_USDT, ETH_CRO, etc. Omit for 'all'
     * @return a list of trades sorting by time ascending
     */
    Flux<Trade> getTrades(String instrument);

}
