package com.example.crypto.service;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

import com.example.crypto.client.CryptoClient;
import com.example.crypto.dto.CandleStickCollection;
import com.example.crypto.dto.CandleStickDto;
import com.example.crypto.dto.Response;
import com.example.crypto.dto.TradeDto;
import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;

@Service
public class CryptoServiceImpl implements CryptoService {

    private final CryptoClient client;

    public CryptoServiceImpl(CryptoClient client) {
        this.client = client;
    }

    @Override
    public Flux<CandleStick> getCandleSticks(String instrument, String interval) {
        return client.getCandleSticks(instrument, interval)
            .map(Response::getResult)
            .flatMapIterable(CandleStickCollection::getData)
            .map(CandleStickDto::build);
    }

    @Override
    public Flux<Trade> getTrades(String instrument) {
        return client.getTrades(instrument)
            .flatMapIterable(Response::getResult)
            .map(TradeDto::build);
    }

}
