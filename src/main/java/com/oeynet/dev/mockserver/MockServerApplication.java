package com.oeynet.dev.mockserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MockServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(MockServerApplication.class, args);
    }

}

