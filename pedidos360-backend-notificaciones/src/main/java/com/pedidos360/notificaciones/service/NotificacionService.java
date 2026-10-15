package com.pedidos360.notificaciones.service;

import com.pedidos360.notificaciones.dto.PedidoEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    public void procesarNotificacionPedido(PedidoEventDTO evento) {
        log.info("Procesando negocio para el pedido #{} del cliente: {}", 
                evento.getIdPedido(), evento.getCorreoCliente());
    }
}