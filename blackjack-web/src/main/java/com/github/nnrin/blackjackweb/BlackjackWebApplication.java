package com.github.nnrin.blackjackweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlackjackWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlackjackWebApplication.class, args);
    }
    // todo: cors needs to be adjusted to allow frontend to communicate w/ backend
}
