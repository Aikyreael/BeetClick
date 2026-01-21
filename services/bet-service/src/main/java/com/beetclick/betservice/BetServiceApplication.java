package com.beetclick.betservice;

import com.beetclick.common.env.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BetServiceApplication {

    public static void main(String[] args) {
        DotenvLoader.loadFromProjectRootIfPresent();
        SpringApplication.run(BetServiceApplication.class, args);
    }

}
