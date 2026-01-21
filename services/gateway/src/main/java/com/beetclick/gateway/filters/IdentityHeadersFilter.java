package com.beetclick.gateway.filters;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class IdentityHeadersFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(IdentityHeadersFilter.class);

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        var reqBuilder = exchange.getRequest().mutate()
                .header("X-Request-Id", requestId)
                .headers(h -> {
                    h.remove("X-User-Id");
                    h.remove("X-User-Email");
                    h.remove("X-User-Role");
                });

        ServerWebExchange ex2 = exchange.mutate().request(reqBuilder.build()).build();

        String finalRequestId = requestId;
        return ex2.getPrincipal()
                .ofType(Authentication.class)
                .filter(Authentication::isAuthenticated)
                .flatMap(auth -> {

                    String email = String.valueOf(auth.getPrincipal());

                    String userId = auth.getCredentials() != null ? String.valueOf(auth.getCredentials()) : null;
                    if (userId != null && userId.isBlank()) userId = null;
                    if ("n/a".equalsIgnoreCase(userId)) userId = null;

                    String role = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .filter(a -> a.startsWith("ROLE_"))
                            .findFirst()
                            .orElse("ROLE_USER")
                            .replace("ROLE_", "");

                    var mut = ex2.getRequest().mutate()
                            .header("X-User-Email", email)
                            .header("X-User-Role", role);

                    if (userId != null) {
                        mut.header("X-User-Id", userId);
                    }

                    var req3 = mut.build();

                    log.debug("[{}] {} {} -> inject X-User-Id={}, X-User-Email={}, X-User-Role={}",
                            finalRequestId,
                            ex2.getRequest().getMethod(),
                            ex2.getRequest().getURI().getPath(),
                            userId,
                            email,
                            role
                    );

                    return chain.filter(ex2.mutate().request(req3).build());
                })
                .switchIfEmpty(chain.filter(ex2));
    }
}

