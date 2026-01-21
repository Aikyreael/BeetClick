package com.beetclick.gateway;

import com.beetclick.common.env.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

import java.util.Map;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class GatewayApplication {

    public static void main(String[] args) {
        DotenvLoader.loadFromProjectRootIfPresent();
        SpringApplication app = new SpringApplication(GatewayApplication.class);

        String gatewayPort = System.getenv().getOrDefault("GATEWAY_PORT", "8080");
        app.setDefaultProperties(Map.of("server.port", gatewayPort));

        app.run(args);
    }
}
