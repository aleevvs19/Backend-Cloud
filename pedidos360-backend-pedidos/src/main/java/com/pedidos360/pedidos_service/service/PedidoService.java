package com.pedidos360.pedidos_service.service;

import com.pedidos360.pedidos_service.entity.DetallePedido;
import com.pedidos360.pedidos_service.entity.Pedido;
import com.pedidos360.pedidos_service.entity.Producto;
import com.pedidos360.pedidos_service.repository.PedidoRepository;
import com.pedidos360.pedidos_service.repository.ProductoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        BigDecimal totalCalculado = BigDecimal.ZERO;

        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + detalle.getProductoId()));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            detalle.setNombreProducto(producto.getNombre());
            detalle.setPrecioUnitario(producto.getPrecio());
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(subtotal);

            totalCalculado = totalCalculado.add(subtotal);
        }

        pedido.setTotal(totalCalculado);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        try {
            rabbitTemplate.convertAndSend("pedidosExchange", "pedido.creado", 
                    "Nuevo Pedido #" + pedidoGuardado.getId() + " creado para sucursal " + pedidoGuardado.getSucursalId());
        } catch (Exception e) {
            System.err.println("Error enviando a RabbitMQ: " + e.getMessage());
        }

        return pedidoGuardado;
    }

    public List<Pedido> obtenerPorSucursal(Long sucursalId) {
        return pedidoRepository.findBySucursalId(sucursalId);
    }

    public Pedido cambiarEstado(Long pedidoId, Pedido.EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(nuevoEstado);
        
        Pedido actualizado = pedidoRepository.save(pedido);

        try {
            rabbitTemplate.convertAndSend("pedidosExchange", "pedido.actualizado", 
                    "Pedido #" + pedidoId + " cambio a estado: " + nuevoEstado);
        } catch (Exception e) {
            System.err.println("Error publicando en RabbitMQ: " + e.getMessage());
        }

        return actualizado;
    }
}