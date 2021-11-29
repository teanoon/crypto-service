package com.example.crypto.service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import reactor.core.publisher.Flux;

import com.example.crypto.client.CryptoService;
import com.example.crypto.model.CandleStick;
import com.example.crypto.model.Trade;
import com.example.crypto.model.Trade.Side;
import com.example.crypto.util.CryptoUtil;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = CryptoReconciliationImplTest.TestConfiguration.class, webEnvironment = WebEnvironment.NONE)
public class CryptoReconciliationImplTest {

    @Autowired
    private CryptoReconciliation reconciliation;

    @Test
    public void reconciliateSuccess() {
        reconciliation.reconciliate("BTC_USDT", "2s", 5).block();
    }

    @Test
    public void aggregateTradeSuccess() {
        // given a new trade
        var aggregation = ((CryptoReconciliationImpl) reconciliation).aggregateTrade(1L);
        var actual = ((CryptoReconciliationImpl) reconciliation).buildCandleStick();
        var trade = buildUnitTrade(BigDecimal.TEN);

        // when aggregates
        aggregation.apply(actual, trade);

        // then the result matches
        assertEquals(
            new CandleStick(
                actual.getTimestamp(),
                BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.TEN, BigDecimal.TEN,
                BigDecimal.ONE.setScale(6)),
            actual);

        // when add a biggest trade
        var highestPrice = BigDecimal.valueOf(Integer.MAX_VALUE);
        trade = buildUnitTrade(highestPrice);
        aggregation.apply(actual, trade);

        // then the result matches
        assertEquals(
            new CandleStick(
                actual.getTimestamp(),
                BigDecimal.TEN, highestPrice,
                highestPrice, BigDecimal.TEN,
                BigDecimal.valueOf(2).setScale(6)),
            actual);

        // when add a smallest trade
        var lowestPrice = BigDecimal.valueOf(Integer.MIN_VALUE);
        trade = buildUnitTrade(lowestPrice);
        aggregation.apply(actual, trade);

        // then the result matches
        assertEquals(
            new CandleStick(
                actual.getTimestamp(),
                BigDecimal.TEN, lowestPrice,
                highestPrice, lowestPrice,
                BigDecimal.valueOf(3).setScale(6)),
            actual);
    }

    private Trade buildUnitTrade(BigDecimal price) {
        return new Trade(
            current().nextLong(1_000_000), "BTC_USDT", Side.BUY,
            price, BigDecimal.ONE,
            new Date());
    }

    @Configuration
    @Import(CryptoReconciliationImpl.class)
    public static class TestConfiguration {

        @Bean
        public CryptoService cryptoService() {
            return new CryptoService() {

                @Override
                public Flux<CandleStick> getCandleSticks(String instrument, String intervalString, int epochs) {
                    var interval = CryptoUtil.parseInterval(intervalString);
                    var intervalInMillis = interval.get(ChronoUnit.SECONDS) * 1000;
                    var currentEpochStart = (long) Math.floor(System.currentTimeMillis() / intervalInMillis) * intervalInMillis;
                    return Flux.range(0, 20)
                        .map(offset -> new CandleStick(
                            currentEpochStart + intervalInMillis * (offset - 19),
                            BigDecimal.ZERO, BigDecimal.valueOf(9),
                            BigDecimal.valueOf(9), BigDecimal.ZERO,
                            BigDecimal.TEN));
                }

                @Override
                public Flux<Trade> getTrades(String instrument) {
                    return Flux.range(0, 10)
                        .map(value -> new Trade(
                            System.currentTimeMillis() + value - 20,
                            instrument,
                            Side.BUY,
                            BigDecimal.valueOf(value), BigDecimal.ONE,
                            new Date(System.currentTimeMillis() + value - 20)));
                }

            };
        }

    }

}
