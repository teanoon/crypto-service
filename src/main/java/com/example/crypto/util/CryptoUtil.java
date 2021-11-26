package com.example.crypto.util;

import java.time.Duration;
import java.util.regex.Pattern;

public class CryptoUtil {

    private static final Pattern INTERVAL_PATTERN = Pattern.compile("^((?<day>\\d+?D)|(?<minute>\\d+?[hms]))$");

    public static Duration parseInterval(String interval) {
        var matches = INTERVAL_PATTERN.matcher(interval);
        if (!matches.find()) {
            throw new IllegalArgumentException("Bad interval: " + interval);
        }
        var builder = new StringBuilder("P");
        if (matches.group("day") != null) {
            builder.append(matches.group("day"));
        }
        if (matches.group("minute") != null) {
            builder.append('T').append(matches.group("minute"));
        }
        return Duration.parse(builder.toString());
    }

}
