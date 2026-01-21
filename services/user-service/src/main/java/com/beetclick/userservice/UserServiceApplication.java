package com.beetclick.userservice;

import com.beetclick.common.env.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        DotenvLoader.loadFromProjectRootIfPresent();
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
