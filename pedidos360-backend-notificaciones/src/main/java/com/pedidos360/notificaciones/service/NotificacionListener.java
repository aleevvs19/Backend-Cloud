package com.pedidos360.notificaciones.service;

import com.pedidos360.notificaciones.config.RabbitMQConfig;
import com.pedidos360.notificaciones.dto.PedidoEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificacionListener {

    @RabbitListener(
            queues = RabbitMQConfig.QUEUE_NOTIFICACIONES
    )
    public void recibirMensajePedido(PedidoEventDTO pedido) {

        System.out.println("=========================================");
        System.out.println("🔔 ¡NUEVA ALERTA DE PEDIDO RECIBIDA! 🔔");
        System.out.println("Detalles del DTO: " + pedido);
        System.out.println("=========================================");
    }
}