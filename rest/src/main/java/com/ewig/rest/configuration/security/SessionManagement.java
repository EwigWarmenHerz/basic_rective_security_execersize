package com.ewig.rest.configuration.security;

import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

//@Configuration
public class SessionManagement {
    //@Bean
    public WebSessionManager webSessionManager() {
        return exchange -> {
            Mono<WebSession> sessionMono = exchange.getSession();
            return sessionMono.flatMap(session -> {
                if (session == null) {
                    return Mono.empty();  // Return empty Mono if session is null
                }
                if (!session.isStarted()) {
                    session.start();
                    return Mono.just(session);
                } else {
                    return Mono.justOrEmpty(session);
                }
            });
        };
    }

    //@Bean
    public WebFilter redirectFilter(ServerRedirectStrategy redirectStrategy) {
        return (exchange, chain) -> exchange.getSession()
                .flatMap(session -> {
                    if (session == null || !session.isStarted()) {
                        try {
                            return redirectStrategy.sendRedirect(exchange, new URI("/login")); // Redirect to login page
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return chain.filter(exchange);
                });
    }

}
