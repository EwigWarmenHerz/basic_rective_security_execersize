package com.ewig.database.configuration.security.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
@Component
public class JwtFilter implements WebFilter {
    private final LogoutService blackListedTokens;

    public JwtFilter(LogoutService blackListedTokens) {
        this.blackListedTokens = blackListedTokens;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        var path = request.getPath().value();
        if(path.contains("free_access") || path.contains("swagger") || path.contains("api-docs")) return chain.filter(exchange);
        var auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return switch (auth){
            case String a when !a.startsWith("Bearer ") -> Mono.error(new Throwable("invalid auth"));
            case String a when blackListedTokens.isBlackListed(a.replace("Bearer ", "")) -> Mono.error(new Throwable("Invalid token"));
            case null -> Mono.error(new Throwable("no token was found"));
            default -> {
                System.out.println(blackListedTokens.getBlackListJwt());
                var token = auth.replace("Bearer ", "");
                exchange.getAttributes().put("token", token);
                yield  chain.filter(exchange);
            }
        };

    }
}
