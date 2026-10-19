package com.pedidos360.notificaciones.service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
@Service
public class DeadLetterListener {
 private static final Logger log=LoggerFactory.getLogger(DeadLetterListener.class);
 // No consumir la DLQ: conservar los mensajes para inspección y recuperación manual.
 // Los rechazos se registran en NotificacionListener.
}
