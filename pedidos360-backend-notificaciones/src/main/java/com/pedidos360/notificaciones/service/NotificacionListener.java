package com.pedidos360.notificaciones.service;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
@Service
public class NotificacionListener {
  private static final Logger log = LoggerFactory.getLogger(NotificacionListener.class);
  @RabbitListener(queues = "${pedidos360.rabbit.queue}", containerFactory = "manualAckFactory")
  public void recibir(Message message, Channel channel) throws IOException {
    long tag = message.getMessageProperties().getDeliveryTag();
    String payload = new String(message.getBody(), StandardCharsets.UTF_8);
    try {
      String[] campos = payload.split("\\|", -1);
      if (campos.length != 4 || campos[0].isBlank() || campos[1].isBlank()) {
        throw new IllegalArgumentException("Formato de evento inválido");
      }
      log.info("Notificación de pedido id={}, cliente={}, estado={}, total={}",campos[0],campos[1],campos[2],campos[3]);
      channel.basicAck(tag,false);
    } catch (IllegalArgumentException e) {
      log.error("Evento inválido, enviado a DLQ: {} - {}", payload, e.getMessage());
      channel.basicReject(tag,false);
    } catch (Exception e) {
      log.error("Error inesperado procesando evento. Se envía a DLQ",e);
      channel.basicNack(tag,false,false);
    }
  }
}
