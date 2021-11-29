package com.example.crypto.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import com.example.crypto.client.CryptoService;
import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;
import com.example.crypto.util.CryptoUtil;

@Slf4j
@Service
public class CryptoReconciliationImpl implements CryptoReconciliation {

    private final CryptoService cryptoService;
    private final Duration requestInterval = Duration.ofMillis(1100);

    public CryptoReconciliationImpl(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    public Mono<Void> reconciliate(String instrument, String intervalString, int epochCount) {
        var interval = CryptoUtil.parseInterval(intervalString);
        var intervalInMillis = interval.get(ChronoUnit.SECONDS) * 1000;
        var epochEnd = epochEnd(new Date(), epochCount, intervalInMillis);
        var aggregator = new ConcurrentHashMap<Long, CandleStick>();
        var terminationSignal = Sinks.<Void>one();
        return Flux.interval(requestInterval)
            .flatMap(v -> cryptoService.getTrades(instrument))
            .distinct(Trade::getId)
            .takeWhile(trade -> trade.getDate().getTime() < epochEnd)
            .groupBy(trade -> epochStart(trade.getDate(), intervalInMillis))
            // .sort(Comparator.comparing(GroupedFlux::key))
            // skip the first and drop last one to take all completed epochs
            // .skip(1).take(epochCount + 1).take(epochCount)
            .flatMap(trades -> trades
                .sort(Comparator.comparing(Trade::getDate))
                .reduce(buildCandleStick(), aggregateTrade(intervalInMillis)))
            .doOnNext(t -> log.info("actual: {}, {}", epochStart(new Date(t.getTimestamp()), intervalInMillis), t))
            .doOnNext(stick -> reconciliate(aggregator, stick))
            .thenEmpty(cryptoService
                .getCandleSticks(instrument, intervalString, epochCount + 2)
                .doOnNext(t -> log.info("expected: {}, {}", epochStart(new Date(t.getTimestamp()), intervalInMillis), t))
                .doOnNext(stick -> reconciliate(aggregator, stick))
                .doOnComplete(() -> terminationSignal.tryEmitEmpty())
                .then())
            .then();
    }

    private void reconciliate(Map<Long, CandleStick> aggregator, CandleStick stick) {
        // CAS eliminates race condition
        var first = aggregator.put(stick.getTimestamp(), stick);
        if (first == null) {
            return;
        }
        if (first.equals(stick)) {
            log.info("Candle stick matches trades at {}", first.getTimestamp());
        } else {
            log.error("Candle stick doesn't match trades at {}", first.getTimestamp());
        }
        // disconnect from GC roots
        aggregator.remove(stick.getTimestamp());
    }

    CandleStick buildCandleStick() {
        return new CandleStick(
            null, null, null,
            BigDecimal.ZERO,
            BigDecimal.valueOf(Long.MAX_VALUE),
            BigDecimal.ZERO);
    }

    BiFunction<CandleStick, Trade, CandleStick> aggregateTrade(Long intervalInMillis) {
        return (CandleStick stick, Trade trade) -> {
            if (stick.getTimestamp() == null) {
                stick.setTimestamp(epochStart(trade.getDate(), intervalInMillis) * intervalInMillis);
            }
            if (stick.getOpenPrice() == null) {
                stick.setOpenPrice(trade.getPrice());
            }
            stick.setClosePrice(trade.getPrice());
            stick.setHighPrice(stick.getHighPrice().max(trade.getPrice()));
            stick.setLowPrice(stick.getLowPrice().min(trade.getPrice()));
            stick.setVolume(stick.getVolume().add(trade.getQuantity()).setScale(6));
            return stick;
        };
    }

    private Long epochStart(Date date, Long intervalInMillis) {
        return Math.floorDiv(date.getTime(), intervalInMillis);
    }

    private Long epochEnd(Date date, int epochCount, Long intervalInMillis) {
        return (epochStart(date, intervalInMillis) + epochCount + 1 + 1) * intervalInMillis;
    }

}
