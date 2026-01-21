package com.beetclick.matchservice;

import com.beetclick.common.env.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatchServiceApplication {

    public static void main(String[] args) {
        DotenvLoader.loadFromProjectRootIfPresent();
        SpringApplication.run(MatchServiceApplication.class, args);
    }

}
