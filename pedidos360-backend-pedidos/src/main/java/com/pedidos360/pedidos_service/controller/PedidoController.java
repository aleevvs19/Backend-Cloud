package com.pedidos360.pedidos_service.controller;

import com.pedidos360.pedidos_service.entity.Pedido;
import com.pedidos360.pedidos_service.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("preferred_username");
        if (email == null) {
            email = jwt.getSubject();
        }
        pedido.setUsuarioEmail(email);

        return ResponseEntity.ok(pedidoService.crearPedido(pedido));
    }

    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<Pedido>> obtenerPorSucursal(@PathVariable Long sucursalId) {
        return ResponseEntity.ok(pedidoService.obtenerPorSucursal(sucursalId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> cambiarEstado(@PathVariable Long id, @RequestParam Pedido.EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado));
    }
}