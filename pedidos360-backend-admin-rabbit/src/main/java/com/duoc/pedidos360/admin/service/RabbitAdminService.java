package com.duoc.pedidos360.admin.service;

import java.util.Properties;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.stereotype.Service;

import com.duoc.pedidos360.admin.dto.BindingRequestDTO;
import com.duoc.pedidos360.admin.dto.ExchangeRequestDTO;
import com.duoc.pedidos360.admin.dto.QueueRequestDTO;
import com.duoc.pedidos360.admin.exception.RecursoNoEncontradoException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitAdminService {

    private final AmqpAdmin amqpAdmin;

    // ---------- Colas ----------

    public void crearCola(QueueRequestDTO dto) {
        boolean tieneRoutingKeyMuerta = dto.getDeadLetterRoutingKey() != null && !dto.getDeadLetterRoutingKey().isBlank();
        boolean tieneDlx = dto.getDeadLetterExchange() != null && !dto.getDeadLetterExchange().isBlank();
        if (tieneRoutingKeyMuerta && !tieneDlx) {
            throw new IllegalArgumentException("deadLetterRoutingKey requiere deadLetterExchange");
        }

        QueueBuilder builder = dto.isDurable()
                ? QueueBuilder.durable(dto.getName())
                : QueueBuilder.nonDurable(dto.getName());
        if (dto.isAutoDelete()) {
            builder.autoDelete();
        }
        if (tieneDlx) {
            builder.deadLetterExchange(dto.getDeadLetterExchange());
        }
        if (tieneRoutingKeyMuerta) {
            builder.deadLetterRoutingKey(dto.getDeadLetterRoutingKey());
        }
        if (dto.getMessageTtlMs() != null) {
            builder.ttl(dto.getMessageTtlMs());
        }

        Queue queue = builder.build();
        amqpAdmin.declareQueue(queue);
        log.info("Cola creada: {}", dto.getName());
    }

    public Properties obtenerCola(String name) {
        Properties props = amqpAdmin.getQueueProperties(name);
        if (props == null) {
            throw new RecursoNoEncontradoException("La cola '" + name + "' no existe");
        }
        return props;
    }

    public void eliminarCola(String name) {
        obtenerCola(name); // lanza 404 si no existe
        amqpAdmin.deleteQueue(name);
        log.info("Cola eliminada: {}", name);
    }

    // ---------- Exchanges ----------

    public void crearExchange(ExchangeRequestDTO dto) {
        ExchangeBuilder builder = switch (dto.getType()) {
            case "direct" -> ExchangeBuilder.directExchange(dto.getName());
            case "topic" -> ExchangeBuilder.topicExchange(dto.getName());
            case "fanout" -> ExchangeBuilder.fanoutExchange(dto.getName());
            case "headers" -> ExchangeBuilder.headersExchange(dto.getName());
            default -> throw new IllegalArgumentException("Tipo de exchange inválido: " + dto.getType());
        };
        builder.durable(dto.isDurable());
        if (dto.isAutoDelete()) {
            builder.autoDelete();
        }
        Exchange exchange = builder.build();
        amqpAdmin.declareExchange(exchange);
        log.info("Exchange creado: {} ({})", dto.getName(), dto.getType());
    }

    public void eliminarExchange(String name) {
        if (name.startsWith("amq.")) {
            throw new IllegalArgumentException("No se pueden eliminar exchanges reservados (amq.*)");
        }
        amqpAdmin.deleteExchange(name);
        log.info("Exchange eliminado: {}", name);
    }

    // ---------- Bindings ----------

    public void crearBinding(BindingRequestDTO dto) {
        amqpAdmin.declareBinding(aBinding(dto));
        log.info("Binding creado: {} -> {} [{}]", dto.getExchange(), dto.getDestination(), dto.getRoutingKey());
    }

    public void eliminarBinding(BindingRequestDTO dto) {
        amqpAdmin.removeBinding(aBinding(dto));
        log.info("Binding eliminado: {} -> {} [{}]", dto.getExchange(), dto.getDestination(), dto.getRoutingKey());
    }

    private Binding aBinding(BindingRequestDTO dto) {
        return new Binding(
                dto.getDestination(),
                DestinationType.valueOf(dto.getDestinationType()),
                dto.getExchange(),
                dto.getRoutingKey() == null ? "" : dto.getRoutingKey(),
                null);
    }
}