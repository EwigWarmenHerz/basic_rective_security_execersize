package com.ewig.rest.configuration.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public")
public class Public {

    @GetMapping("/hello")
    public Mono<String> hello(){
        return Mono.just("world");
    }
}
