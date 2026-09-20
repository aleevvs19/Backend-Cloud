package com.pedidos360.pedidos_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "pedidosExchange";

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
}