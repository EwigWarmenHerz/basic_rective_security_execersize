package com.ewig.rest.configuration.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
@Configuration
public class RabbitConfig {

    @Value("${spring.rabbitmq.username}")
    private String rabbitUser;
    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    private final static Logger log = LoggerFactory.getLogger(RabbitConfig.class);

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory, @Value("${queues.lecture}") String queue){
        var admin = new RabbitAdmin(connectionFactory);
        admin.declareQueue(QueueBuilder.durable(queue).withArgument("x-message-ttl",60000).build());
        return admin;
    }

    @Bean
    public RestTemplate rabbitMqConsumerRequest(RestTemplateBuilder builder){
        return builder.basicAuthentication(rabbitUser,rabbitPassword).build();
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }


    @Bean
    public Queue eventsQueue(@Value("${queues.lecture}") String queue){
        log.info(queue);
        return QueueBuilder.durable(queue).withArgument("x-message-ttl",60000).build();
    }

}
