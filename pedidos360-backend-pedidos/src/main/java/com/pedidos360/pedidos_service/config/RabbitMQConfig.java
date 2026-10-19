package com.pedidos360.pedidos_service.config;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
@Configuration
public class RabbitMQConfig {
  @Bean
  public TopicExchange pedidosExchange(@Value("${pedidos360.rabbit.exchange}") String exchange) {
    return new TopicExchange(exchange, true, false);
  }
}
