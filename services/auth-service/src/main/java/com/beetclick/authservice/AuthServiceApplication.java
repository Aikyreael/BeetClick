package com.beetclick.authservice;

import com.beetclick.common.env.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        DotenvLoader.loadFromProjectRootIfPresent();
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
