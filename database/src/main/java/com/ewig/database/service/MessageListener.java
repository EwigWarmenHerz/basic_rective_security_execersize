package com.ewig.database.service;

import com.ewig.database.configuration.security.jwt.JwtProvider;
import com.ewig.database.entity.LoginDto;
import com.ewig.database.entity.MessageDto;
import com.ewig.database.entity.UserRole;
import com.ewig.database.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class MessageListener {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    public MessageListener(ObjectMapper objectMapper, UserRepository userRepository, JwtProvider jwtProvider) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;

        this.jwtProvider = jwtProvider;
    }

    @RabbitListener(queues = "${queues.lecture}")
    @SendTo
    public Mono<String> receiveMessage(String jsonMessage) throws JsonProcessingException {
        var event = objectMapper.readValue(jsonMessage, MessageDto.class);
        return switch (event.messageType()){
            case LOGIN -> {
                var message = objectMapper.readValue(event.message(), LoginDto.class);
                yield userRepository.findByEmailAndPassword(message.email(),message.password())
                        .flatMap(user -> {
                            var token = switch (UserRole.values()[user.getUserRole() -1]){
                                case USER -> jwtProvider.generateToken(user.getEmail(), List.of(UserRole.USER.toString()));
                                case ADMIN -> jwtProvider.generateToken(user.getEmail(), List.of(UserRole.USER.toString(),UserRole.ADMIN.toString()));
                            };
                            return Mono.just("{\"token\": " + " \"" + token + "\"}") ;
                        });
            }
            case SINGLE_USER -> {
                var id = objectMapper.readValue(jsonMessage, Long.class);
                yield  userRepository.findById(Long.valueOf(id))
                        .map(user -> {
                            try {
                                return objectMapper.writeValueAsString(user);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
            case ALL_USER -> userRepository.findAll().collectList().map(users -> {
                try {
                    return objectMapper.writeValueAsString(users);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        };

    }
}
