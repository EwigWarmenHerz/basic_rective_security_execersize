package com.ewig.rest.controller;

import com.ewig.rest.Entity.MessageDto;
import com.ewig.rest.Entity.MessageType;
import com.ewig.rest.Entity.UserDto;
import com.ewig.rest.configuration.rabbit.MessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/private")
public class Private {
    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;


    public Private(MessageSender messageSender, ObjectMapper objectMapper) {
        this.messageSender = messageSender;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/get_user")
    public Mono<UserDto>getUser(@RequestParam long id) throws JsonProcessingException {
        var message = new MessageDto(MessageType.SINGLE_USER,String.valueOf(id));
        var json = objectMapper.writeValueAsString(message);
        return messageSender.sendMessage(json)
                .map(this::parseSingleUserResponse);

    }

    @GetMapping("/get_all_user")
    public Mono<List<UserDto>> getAllUsers(@RequestParam long id) throws JsonProcessingException {
        var json = objectMapper.writeValueAsString(id);
        return messageSender.sendMessage(json)
                .map(this::parseListUserDto);

    }


    private UserDto parseSingleUserResponse(String response) {
        try {
            return objectMapper.readValue(response, UserDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<UserDto> parseListUserDto(String response){
        try {
            return objectMapper.readValue(response, new TypeReference<ArrayList<UserDto>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
