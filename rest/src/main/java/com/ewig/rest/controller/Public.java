package com.ewig.rest.controller;

import com.ewig.rest.Entity.LoginDto;
import com.ewig.rest.Entity.MessageDto;
import com.ewig.rest.Entity.MessageType;
import com.ewig.rest.configuration.rabbit.MessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public")
public class Public {

    private final ObjectMapper objectMapper;
    private final MessageSender messageSender;

    public Public(ObjectMapper objectMapper, MessageSender messageSender) {
        this.objectMapper = objectMapper;
        this.messageSender = messageSender;
    }

    @GetMapping("/hello")
    public Mono<String> hello(){
        return Mono.just("world");
    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody LoginDto loginDto) throws JsonProcessingException {
        var message = objectMapper.writeValueAsString(loginDto);
        var json = objectMapper.writeValueAsString(new MessageDto(MessageType.LOGIN,message));

        return messageSender.sendMessage(json);
    }
}
