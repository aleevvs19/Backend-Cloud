package com.pedidos360.notificaciones.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.pedidos360.notificaciones.dto.PedidoEventDTO;

@Service
public class NotificacionListener {

    // Escucha continuamente la cola de notificaciones
    @RabbitListener(queues = "notificaciones_queue")
    public void recibirMensajePedido(PedidoEventDTO pedido) {
        
        System.out.println("=========================================");
        System.out.println("🔔 ¡NUEVA ALERTA DE PEDIDO RECIBIDA! 🔔");
        System.out.println("Detalles del DTO: " + pedido.toString());
        System.out.println("=========================================");
        
    }
}