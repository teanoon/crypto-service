package com.example.crypto.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import reactor.core.publisher.Mono;

import com.example.crypto.dto.CandleStickCollection;
import com.example.crypto.dto.Response;
import com.example.crypto.dto.TradeDto;

@FeignClient(name = "crypto-client", url = "${crypto.host}", path = "${crypto.path}")
public interface CryptoClient {

    @GetMapping("/get-candlestick")
    Mono<Response<CandleStickCollection>> getCandleSticks(
        @RequestParam("instrument_name")
        String instrumentName,
        @RequestParam("timeframe")
        String timeframe);

    @GetMapping("/get-trades")
    Mono<Response<List<TradeDto>>> getTrades(
        @RequestParam("instrument_name")
        String instrumentName);

}
