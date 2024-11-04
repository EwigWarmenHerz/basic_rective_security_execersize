package com.ewig.rest.configuration.security.jwt;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
public class LogoutService implements ServerLogoutHandler {
    private static final Set<String> blackListJwt = new HashSet<>();

    public void addToken(String token){
        blackListJwt.add(token);
    }

    public boolean isBlackListed(String token){
        return blackListJwt.contains(token);
    }
    public Set<String> getBlackListJwt(){
        return blackListJwt;
    }

    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        return null;
    }

    private String extractTokenFromRequest(ServerHttpRequest request) {
        return request.getHeaders().getFirst("Authorization").replaceAll("Bearer ", "");
    }
}
