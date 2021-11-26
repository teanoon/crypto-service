package com.example.crypto;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.example.crypto.client.CryptoClient;
import com.example.crypto.service.CryptoReconciliation;

@SpringBootApplication
@EnableFeignClients(basePackageClasses = CryptoClient.class)
public class Application implements CommandLineRunner {

    private final CryptoReconciliation reconciliation;

    public Application(CryptoReconciliation reconciliation) {
        this.reconciliation = reconciliation;
    }

    @Override
    public void run(String... args) throws Exception {
        reconciliation.reconciliate("instrument", "1m", 10);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
