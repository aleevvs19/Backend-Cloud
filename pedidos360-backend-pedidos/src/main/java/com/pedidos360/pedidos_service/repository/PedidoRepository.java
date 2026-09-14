package com.pedidos360.pedidos_service.repository;

import com.pedidos360.pedidos_service.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findBySucursalId(Long sucursalId);
    List<Pedido> findByUsuarioEmail(String usuarioEmail);
}