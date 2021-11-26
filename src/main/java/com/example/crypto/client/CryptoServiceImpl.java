package com.example.crypto.client;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;

import com.example.crypto.client.dto.CandleStickDto;
import com.example.crypto.client.dto.DataCollection;
import com.example.crypto.client.dto.Response;
import com.example.crypto.client.dto.TradeDto;
import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;

@Component
public class CryptoServiceImpl implements CryptoService {

    private final CryptoClient client;

    public CryptoServiceImpl(CryptoClient client) {
        this.client = client;
    }

    @Override
    public Flux<CandleStick> getCandleSticks(String instrument, String interval) {
        return client.getCandleSticks(instrument, interval, 3)
            .map(Response::getResult)
            .flatMapIterable(DataCollection::getData)
            .map(CandleStickDto::build);
    }

    @Override
    public Flux<Trade> getTrades(String instrument) {
        return client.getTrades(instrument)
            .map(Response::getResult)
            .flatMapIterable(DataCollection::getData)
            .map(TradeDto::build);
    }

}
