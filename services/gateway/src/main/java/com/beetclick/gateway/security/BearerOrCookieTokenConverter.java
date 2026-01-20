package com.beetclick.gateway.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class BearerOrCookieTokenConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String token = resolveToken(exchange);
        if (!looksLikeJwt(token)) {
            return Mono.empty();
        }
        return Mono.just(new BearerTokenAuthenticationToken(token));
    }

    private String resolveToken(ServerWebExchange exchange) {
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            return auth.substring(7).trim();
        }

        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("jwt");
        return cookie != null ? cookie.getValue() : null;
    }

    private boolean looksLikeJwt(String token) {
        if (!StringUtils.hasText(token)) return false;
        int dots = 0;
        for (int i = 0; i < token.length(); i++) if (token.charAt(i) == '.') dots++;
        return dots == 2;
    }
}
