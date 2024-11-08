package com.ewig.rest.configuration.security;


import com.ewig.rest.configuration.security.jwt.JwtAuthManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {
    private final JwtAuthManager jwtAuthManager;

    public SecurityContextRepository(JwtAuthManager jwtAuthManager) {
        this.jwtAuthManager = jwtAuthManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        var token = exchange.getAttribute("token");
        return jwtAuthManager.authenticate(new UsernamePasswordAuthenticationToken(token,token)).map(SecurityContextImpl::new);
    }
}
