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

@SpringBootTest(classes = CryptoReconciliationImplTest.TestConfiguration.class, webEnvironment = WebEnvironment.NONE)
public class CryptoReconciliationImplTest {

    @Autowired
    private CryptoReconciliation reconciliation;

    @Test
    public void reconciliateSuccess() {
        reconciliation.reconciliate("BTC_USDT", "2s", 3).block();
    }

    @Configuration
    @Import(CryptoReconciliationImpl.class)
    public static class TestConfiguration {

        @Bean
        public CryptoService cryptoService() {
            return new CryptoService() {

                @Override
                public Flux<CandleStick> getCandleSticks(String instrument, String intervalString) {
                    var interval = CryptoUtil.parseInterval(intervalString);
                    var intervalInMillis = interval.get(ChronoUnit.SECONDS) * 1000;
                    var currentPeriodStart = (long) Math.floor(System.currentTimeMillis() / intervalInMillis) * intervalInMillis;
                    return Flux.range(0, 3)
                        .map(offset -> new CandleStick(
                            currentPeriodStart + intervalInMillis * (offset - 2), BigDecimal.ONE, BigDecimal.valueOf(9),
                            BigDecimal.valueOf(9), BigDecimal.ONE,
                            BigDecimal.valueOf(10)));
                }

                @Override
                public Flux<Trade> getTrades(String instrument) {
                    return Flux.range(0, 10)
                        .map(value -> new Trade(
                            System.currentTimeMillis() + value - 20,
                            instrument,
                            Side.BUY,
                            BigDecimal.valueOf(value), BigDecimal.ONE,
                            new Date()));
                }

            };
        }

    }

}
