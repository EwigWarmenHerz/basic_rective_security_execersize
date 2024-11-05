package com.ewig.database.service;

import com.ewig.database.configuration.security.jwt.JwtProvider;
import com.ewig.database.entity.LoginDto;
import com.ewig.database.entity.MessageDto;
import com.ewig.database.entity.MyUser;
import com.ewig.database.entity.UserRole;
import com.ewig.database.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class MessageListener {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final DatabaseClient databaseClient;

    private final JwtProvider jwtProvider;

    public MessageListener(ObjectMapper objectMapper, UserRepository userRepository, DatabaseClient databaseClient, JwtProvider jwtProvider) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.databaseClient = databaseClient;

        this.jwtProvider = jwtProvider;
    }

    @RabbitListener(queues = "${queues.lecture}")
    @SendTo
    public Mono<String> receiveMessage(String jsonMessage) throws JsonProcessingException {
        var event = objectMapper.readValue(jsonMessage, MessageDto.class);
        System.out.println(event);
        return switch (event.messageType()){
            case LOGIN -> {
                var message = objectMapper.readValue(event.message(), LoginDto.class);
                yield getByEmail(message.email())
                        .flatMap(user -> {
                            if(user.getPassword().equals(message.password())){
                                var token = switch (UserRole.values()[user.getUserRole() -1]){
                                    case USER -> jwtProvider.generateToken(user.getEmail(), List.of(UserRole.USER.toString()));
                                    case ADMIN -> jwtProvider.generateToken(user.getEmail(), List.of(UserRole.USER.toString(),UserRole.ADMIN.toString()));
                                };
                                return Mono.just("{\"token\": " + " \"" + token + "\"}") ;
                            }else return Mono.just("{\"token\": " + " \"" + "error" + "\"}") ;
                        })
                        .switchIfEmpty(Mono.just("User do not exist"));
            }
            case SINGLE_USER -> {
                var id = objectMapper.readValue(event.message(), Long.class);
                System.out.println(id);
                yield  getByid(Long.valueOf(id))
                        .map(user -> {
                            try {
                                return objectMapper.writeValueAsString(user);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
            case ALL_USER -> getAll().map(users -> {
                try {
                    return objectMapper.writeValueAsString(users);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        };

    }

    private Mono<MyUser> getByEmail(String email){
        var query = "SELECT * FROM public.user WHERE email = :email";
        return databaseClient.sql(query)
                .bind("email",email)
                .map((row, data) -> new MyUser(
                        row.get("id", Long.class),
                        row.get("full_name", String.class),
                        row.get("email", String.class),
                        row.get("phone", String.class),
                        row.get("password", String.class),
                        row.get("user_role", Integer.class)

                )).one();
    }

    private Mono<MyUser>getByid(long id){
        var query = "SELECT * FROM public.user WHERE id = :id";
        return databaseClient.sql(query)
                .bind("id",id)
                .map((row, data) -> new MyUser(
                        row.get("id", Long.class),
                        row.get("full_name", String.class),
                        row.get("email", String.class),
                        row.get("phone", String.class),
                        row.get("password", String.class),
                        row.get("user_role", Integer.class)

                )).one();
    }

    private Mono<List<MyUser>>getAll(){
        var query = "SELECT * FROM public.user";
        return databaseClient.sql(query)
                .map((row, data) -> new MyUser(
                        row.get("id", Long.class),
                        row.get("full_name", String.class),
                        row.get("email", String.class),
                        row.get("phone", String.class),
                        row.get("password", String.class),
                        row.get("user_role", Integer.class)

                )).all().collectList();
    }
}
