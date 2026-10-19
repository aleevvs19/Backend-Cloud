package com.pedidos360.notificaciones.config;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.core.AcknowledgeMode;
@Configuration
public class RabbitMQConfig {
  @Bean public TopicExchange pedidosExchange(@Value("${pedidos360.rabbit.exchange}") String name) {
    return new TopicExchange(name, true, false);
  }
  @Bean public DirectExchange deadLetterExchange(@Value("${pedidos360.rabbit.dlx}") String name) {
    return new DirectExchange(name, true, false);
  }
  @Bean public Queue notificacionesQueue(@Value("${pedidos360.rabbit.queue}") String name,
      @Value("${pedidos360.rabbit.dlx}") String dlx,
      @Value("${pedidos360.rabbit.dl-routing}") String dlRouting) {
    return QueueBuilder.durable(name).withArgument("x-dead-letter-exchange",dlx)
        .withArgument("x-dead-letter-routing-key",dlRouting).build();
  }
  @Bean public Queue notificacionesDlq(@Value("${pedidos360.rabbit.dlq}") String name) {
    return QueueBuilder.durable(name).build();
  }
  @Bean public Binding creadoBinding(Queue notificacionesQueue, TopicExchange pedidosExchange,
      @Value("${pedidos360.rabbit.routing-creado}") String routing) {
    return BindingBuilder.bind(notificacionesQueue).to(pedidosExchange).with(routing);
  }
  @Bean public Binding actualizadoBinding(Queue notificacionesQueue, TopicExchange pedidosExchange,
      @Value("${pedidos360.rabbit.routing-actualizado}") String routing) {
    return BindingBuilder.bind(notificacionesQueue).to(pedidosExchange).with(routing);
  }
  @Bean public Binding dlqBinding(Queue notificacionesDlq, DirectExchange deadLetterExchange,
      @Value("${pedidos360.rabbit.dl-routing}") String routing) {
    return BindingBuilder.bind(notificacionesDlq).to(deadLetterExchange).with(routing);
  }
  @Bean public SimpleRabbitListenerContainerFactory manualAckFactory(ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    factory.setDefaultRequeueRejected(false);
    factory.setPrefetchCount(1);
    return factory;
  }
}
