
package com.pedidos360.pedidos_service.controller;

import com.pedidos360.pedidos_service.entity.Pedido;
import com.pedidos360.pedidos_service.service.PedidoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // Obtener el correo desde el token de Microsoft Entra ID
    private String obtenerEmail(Jwt jwt) {

        if (jwt == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Usuario no autenticado"
            );
        }

        // En los tokens v1.0 el correo puede venir en email
        String email = jwt.getClaimAsString("email");

        if (email == null || email.isBlank()) {
            email = jwt.getClaimAsString("preferred_username");
        }

        if (email == null || email.isBlank()) {
            email = jwt.getClaimAsString("upn");
        }

        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No se encontro un correo valido en el token"
            );
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    // 1. Crear un pedido
    @PostMapping
    public ResponseEntity<Pedido> crearPedido(
            @RequestBody Pedido pedido,
            @AuthenticationPrincipal Jwt jwt) {

        String email = obtenerEmail(jwt);

        // No confiar en un correo enviado desde Angular
        pedido.setUsuarioEmail(email);

        Pedido pedidoGuardado = pedidoService.crearPedido(pedido);

        return ResponseEntity.ok(pedidoGuardado);
    }

    // 2. Consultar los pedidos del usuario autenticado
    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<Pedido>> obtenerMisPedidos(
            @AuthenticationPrincipal Jwt jwt) {

        String email = obtenerEmail(jwt);

        List<Pedido> pedidos = pedidoService.obtenerMisPedidos(email);

        return ResponseEntity.ok(pedidos);
    }

    // 3. Consultar los pedidos por sucursal
    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<Pedido>> obtenerPorSucursal(
            @PathVariable Long sucursalId) {

        return ResponseEntity.ok(
            pedidoService.obtenerPorSucursal(sucursalId)
        );
    }

    // 4. Cambiar el estado de un pedido
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Pedido.EstadoPedido estado) {

        return ResponseEntity.ok(
            pedidoService.cambiarEstado(id, estado)
        );
    }
}
