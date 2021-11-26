package com.example.crypto.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Flux;

import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;

@FeignClient(name = "crypto-client", url = "${crypto.host}", path = "${crypto.path}")
public interface CryptoClient {

    /**
     * Retrieves candlesticks (k-line data history) over a given period for an instrument (e.g. BTC_USDT)
     *
     * @param instrument the instrument name, e.g. BTC_USDT, ETH_CRO, etc.
     * @param interval one of 1m 5m 15m 30m 1h 4h 6h 12h 1D 7D 14D 1M
     * @return a list of candle sticks sorting by time ascending
     */
    @GetMapping("/get-candlestick")
    Flux<CandleStick> getCandleSticks(
        @RequestParam("instrument_name")
        String instrument,
        @RequestParam("timeframe")
        String interval);

    /**
     * Fetches the public trades for a particular instrument recently.
     *
     * @param instrument the instrument name, e.g. BTC_USDT, ETH_CRO, etc. Omit for 'all'
     * @return a list of trades sorting by time ascending
     */
    @GetMapping("/get-trades")
    Flux<Trade> getTrades(
        @RequestParam("instrument_name")
        String instrument);

}
