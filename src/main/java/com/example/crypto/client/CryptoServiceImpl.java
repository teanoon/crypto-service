package com.example.crypto.client;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import com.example.crypto.client.dto.CandleStickDto;
import com.example.crypto.client.dto.DataCollection;
import com.example.crypto.client.dto.Response;
import com.example.crypto.client.dto.TradeDto;
import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;

@Slf4j
@Component
public class CryptoServiceImpl implements CryptoService {

    private final CryptoClient client;
    private final Scheduler single;

    public CryptoServiceImpl(CryptoClient client) {
        this.client = client;
        single = Schedulers.newBoundedElastic(1, 100, "crypto-trade");
    }

    @Override
    public Flux<CandleStick> getCandleSticks(String instrument, String interval) {
        return getCandleSticks(instrument, interval, 3);
    }

    @Override
    public Flux<CandleStick> getCandleSticks(String instrument, String interval, int epochs) {
        return client.getCandleSticks(instrument, interval, epochs)
            .map(Response::getResult)
            // .onErrorReturn(new DataCollection<>(Collections.emptyList()))
            .flatMapIterable(DataCollection::getData)
            .map(CandleStickDto::build)
            .subscribeOn(single);
    }

    @Override
    public Flux<Trade> getTrades(String instrument) {
        return client.getTrades(instrument)
            .map(Response::getResult)
            // .onErrorReturn(new DataCollection<>(Collections.emptyList()))
            .doOnNext(result -> log.info("get batch {}", result.getData().size()))
            .flatMapIterable(DataCollection::getData)
            .map(TradeDto::build)
            .subscribeOn(single);
    }

}
