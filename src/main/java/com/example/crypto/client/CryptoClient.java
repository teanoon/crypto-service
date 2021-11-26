package com.example.crypto.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Mono;

import com.example.crypto.client.dto.CandleStickDto;
import com.example.crypto.client.dto.Response;
import com.example.crypto.client.dto.TradeDto;

import reactivefeign.spring.config.ReactiveFeignClient;

@ReactiveFeignClient(name = "crypto-client", url = "${crypto.host}", path = "${crypto.path}")
public interface CryptoClient {

    @GetMapping("/get-candlestick")
    Mono<Response<CandleStickDto>> getCandleSticks(
        @RequestParam("instrument_name")
        String instrument,
        @RequestParam("timeframe")
        String interval,
        @RequestParam("depth")
        int depth);

    @GetMapping("/get-trades")
    Mono<Response<TradeDto>> getTrades(
        @RequestParam("instrument_name")
        String instrument);

}
