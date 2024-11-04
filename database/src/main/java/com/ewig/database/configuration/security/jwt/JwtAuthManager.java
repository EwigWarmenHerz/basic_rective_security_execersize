package com.ewig.database.configuration.security.jwt;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthManager implements ReactiveAuthenticationManager {
    private final JwtProvider jwtProvider;
    private final static Set<String> blacklist = new HashSet<>();

    public JwtAuthManager(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;

    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (authentication.getCredentials() == null) {
            return Mono.empty();
        }
                return Mono.just(authentication)
                .map(auth -> jwtProvider.extractClaims(auth.getCredentials().toString()))
                .onErrorResume(e -> Mono.error(new Throwable("Bad token: " + e.getMessage())))
                .map(claims -> {
                    var roles = claims.get("roles", List.class);
                    var authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.toString())).toList();
                    return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
                });
    }




}
