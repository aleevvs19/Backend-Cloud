package com.pedidos360.notificaciones.consumer;

import com.pedidos360.notificaciones.dto.PedidoEventDTO;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NotificacionConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificacionConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queue.notificaciones:notificaciones.queue}")
    public void recibirMensajePedido(PedidoEventDTO pedido, Channel channel, Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            log.info("=========================================");
            log.info("🔔 ¡NUEVA ALERTA DE PEDIDO RECIBIDA! 🔔");
            log.info("Detalles del DTO: {}", pedido.toString());
            log.info("=========================================");

            if ("ERROR".equalsIgnoreCase(pedido.getEstado())) {
                throw new IllegalArgumentException("Estado invalido detectado.");
            }

            // Confirmación explícita (ACK)
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("❌ Error al procesar el mensaje (Tag: {}): {}", deliveryTag, e.getMessage());

            // NACK sin requeue para desviar a la Dead Letter Queue (DLQ)
            channel.basicNack(deliveryTag, false, false);
            log.warn("Mensaje desviado a la Dead Letter Queue (DLQ).");
        }
    }
}