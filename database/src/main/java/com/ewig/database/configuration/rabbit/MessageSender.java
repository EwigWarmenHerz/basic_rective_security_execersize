package com.ewig.database.configuration.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class MessageSender {

    @Value("exchange.lecture")
    private String exchange;
    @Value("routing_key.lecture")
    private String lectureRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Mono<String> sendMessage(String message) {
        return Mono.just(Objects.requireNonNull(rabbitTemplate.convertSendAndReceive(exchange, lectureRoutingKey, message)).toString());

    }
}
