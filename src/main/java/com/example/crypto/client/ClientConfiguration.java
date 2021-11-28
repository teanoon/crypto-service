package com.example.crypto.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactivefeign.java11.Java11ReactiveOptions;

@Configuration
public class ClientConfiguration {

    @Bean
    public Java11ReactiveOptions java11ReactiveOptions() {
        return new Java11ReactiveOptions.Builder()
            .setRequestTimeoutMillis(1000)
            .build();
    }

}
