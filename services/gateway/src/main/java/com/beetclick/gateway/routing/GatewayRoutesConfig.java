package com.beetclick.gateway.routing;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {

        String authUri = uri("AUTH_SERVICE_HOST", "AUTH_SERVICE_PORT", "localhost", "8081");
        String userUri = uri("USER_SERVICE_HOST", "USER_SERVICE_PORT", "localhost", "8082");
        String matchUri = uri("MATCH_SERVICE_HOST", "MATCH_SERVICE_PORT", "localhost", "8083");
        String betUri = uri("BET_SERVICE_HOST", "BET_SERVICE_PORT", "localhost", "8084");
        String walletUri = uri("WALLET_SERVICE_HOST", "WALLET_SERVICE_PORT", "localhost", "8085");
        String paymentUri = uri("PAYMENT_SERVICE_HOST", "PAYMENT_SERVICE_PORT", "localhost", "8086");
        String notifUri = uri("NOTIFICATION_SERVICE_HOST", "NOTIFICATION_SERVICE_PORT", "localhost", "8087");
        String statsUri = uri("STATS_SERVICE_HOST", "STATS_SERVICE_PORT", "localhost", "8088");
        String aggUri = uri("AGGREGATOR_SERVICE_HOST", "AGGREGATOR_SERVICE_PORT", "localhost", "8089");

        return builder.routes()
                .route("auth", r -> r.path("/auth/**").uri(authUri))
                .route("user", r -> r.path("/users/**").uri(userUri))

                .route("match", r -> r.path("/matches/**").uri(matchUri))
                .route("match-admin", r -> r.path("/admin/matches", "/admin/matches/**").uri(matchUri))

                .route("bet", r -> r.path("/bets/**").uri(betUri))
                .route("wallet", r -> r.path("/wallets/**").uri(walletUri))
                .route("payment", r -> r.path("/payments/**").uri(paymentUri))
                .route("notification", r -> r.path("/notifications/**").uri(notifUri))
                .route("stats", r -> r.path("/stats/**").uri(statsUri))
                .route("aggregator", r -> r.path("/aggregator/**").uri(aggUri))
                .build();
    }

    private String uri(String hostKey, String portKey, String hostDefault, String portDefault) {
        String host = env(hostKey, hostDefault);
        String port = env(portKey, portDefault);
        return "http://" + host + ":" + port;
    }

    private String env(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }
}
