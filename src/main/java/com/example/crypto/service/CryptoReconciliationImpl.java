package com.example.crypto.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import com.google.common.annotations.VisibleForTesting;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import com.example.crypto.client.CryptoClient;
import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;
import com.example.crypto.util.CryptoUtil;

@Slf4j
@Service
public class CryptoReconciliationImpl implements CryptoReconciliation {

    private final CryptoClient client;
    private final Scheduler scheduler;

    @VisibleForTesting
    Duration requestInterval = Duration.ofSeconds(1);

    public CryptoReconciliationImpl(CryptoClient client) {
        this.client = client;
        scheduler = Schedulers.newBoundedElastic(10, 100, "fetch");
    }

    @Override
    public Mono<Void> reconciliate(String instrument, String intervalString, int intervalCount) {
        var interval = CryptoUtil.parseInterval(intervalString);
        var intervalInMillis = interval.get(ChronoUnit.SECONDS) * 1000;
        var aggregator = new ConcurrentHashMap<Long, CandleStick>();
        var terminationSignal = Sinks.<Void>one();
        // collecting trades
        Flux.interval(requestInterval)
            .flatMap(v -> client.getTrades(instrument))
            .distinct(Trade::getId)
            .windowUntilChanged(trade -> (long) Math.floor(trade.getDate().getTime() / intervalInMillis))
            // skip the first and drop last one to take all completed periods
            .skip(1).take(intervalCount)
            .flatMap(trades -> trades.reduce(
                buildCandleStick(),
                aggregateTrade(intervalInMillis)))
            .doOnNext(t -> log.info("actual: {}", new Date(t.getTimestamp())))
            .doOnNext(stick -> reconciliate(aggregator, stick))
            .doOnComplete(() -> terminationSignal.tryEmitEmpty())
            .subscribeOn(scheduler)
            .subscribe();
        // collecting candle sticks
        Flux.interval(interval)
            .flatMap(v -> client.getCandleSticks(instrument, intervalString))
            .distinct(CandleStick::getTimestamp)
            .doOnNext(t -> log.info("expected: {}", new Date(t.getTimestamp())))
            .doOnNext(stick -> reconciliate(aggregator, stick))
            .subscribeOn(scheduler)
            .subscribe();
        return terminationSignal.asMono();
    }

    private void reconciliate(Map<Long, CandleStick> aggregator, CandleStick stick) {
        // CAS eliminates race condition
        var first = aggregator.put(stick.getTimestamp(), stick);
        if (first == null) {
            return;
        }
        var date = new Date(first.getTimestamp());
        if (first.equals(stick)) {
            log.info("Candle stick matches trades at {}", date);
        } else {
            log.error("Candle stick doesn't match trades at {}", date);
        }
        // disconnect from GC roots
        aggregator.remove(stick.getTimestamp());
    }

    private CandleStick buildCandleStick() {
        return new CandleStick(
            null, null, null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO);
    }

    private BiFunction<CandleStick, Trade, CandleStick> aggregateTrade(Long intervalInMillis) {
        return (CandleStick stick, Trade trade) -> {
            if (stick.getTimestamp() == null) {
                stick.setTimestamp((long) Math.floor(trade.getDate().getTime() / intervalInMillis) * intervalInMillis);
            }
            if (stick.getOpenPrice() == null) {
                stick.setOpenPrice(trade.getPrice());
            }
            stick.setClosePrice(trade.getPrice());
            if (stick.getHighPrice().compareTo(trade.getPrice()) < 0) {
                stick.setHighPrice(trade.getPrice());
            }
            if (stick.getLowPrice().compareTo(trade.getPrice()) > 0) {
                stick.setLowPrice(trade.getPrice());
            }
            stick.setVolume(stick.getVolume().add(trade.getPrice().multiply(trade.getQuantity())));
            return stick;
        };
    }

}
