package com.example.crypto.util;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CryptoUtilTest {

    @Test
    public void parseIntervalSuccess() {
        assertEquals(Duration.ofMinutes(1), CryptoUtil.parseInterval("1m"));
        assertEquals(Duration.ofMinutes(5), CryptoUtil.parseInterval("5m"));
        assertEquals(Duration.ofMinutes(60), CryptoUtil.parseInterval("1h"));
        assertEquals(Duration.ofMinutes(720), CryptoUtil.parseInterval("12h"));
        assertEquals(Duration.ofDays(1), CryptoUtil.parseInterval("1D"));
    }

}
