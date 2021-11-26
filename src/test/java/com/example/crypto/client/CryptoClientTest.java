package com.example.crypto.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

import reactivefeign.spring.config.EnableReactiveFeignClients;
import reactivefeign.spring.config.ReactiveFeignAutoConfiguration;

@SpringBootTest(classes = CryptoClientTest.TestConfiguration.class, webEnvironment = WebEnvironment.NONE)
public class CryptoClientTest {

    @Autowired
    private CryptoClient client;

    @Test
    public void getCandleSticksSuccess() {
        var response = client.getCandleSticks("BTC_USDT", "5m", 3).block();
        assertThat(response, hasProperty("method", equalTo("public/get-candlestick")));
    }

    @Test
    public void getTradesSuccess() {
        var response = client.getTrades("BTC_USDT").block();
        assertThat(response, hasProperty("method", equalTo("public/get-trades")));
    }

    @Configuration
    @EnableReactiveFeignClients(basePackageClasses = CryptoClient.class)
    @Import({ReactiveFeignAutoConfiguration.class})
    static class TestConfiguration {

    }

}
