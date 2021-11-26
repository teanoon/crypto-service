package com.example.crypto;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.crypto.client.CryptoClient;
import com.example.crypto.service.CryptoReconciliation;

import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients(basePackageClasses = CryptoClient.class)
public class Application implements CommandLineRunner {

    private final CryptoReconciliation reconciliation;

    public Application(CryptoReconciliation reconciliation) {
        this.reconciliation = reconciliation;
    }

    @Override
    public void run(String... args) throws Exception {
        reconciliation.reconciliate("BTC_USDT", "1m", 2)
            .doFinally(v -> System.exit(0))
            .subscribe();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
