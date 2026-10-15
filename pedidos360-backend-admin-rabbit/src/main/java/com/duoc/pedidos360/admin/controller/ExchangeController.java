package com.duoc.pedidos360.admin.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duoc.pedidos360.admin.dto.ExchangeRequestDTO;
import com.duoc.pedidos360.admin.service.RabbitAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/exchanges")
@RequiredArgsConstructor
public class ExchangeController {

    private final RabbitAdminService adminService;

    @PostMapping
    public ResponseEntity<Map<String, String>> crear(@Valid @RequestBody ExchangeRequestDTO dto) {
        adminService.crearExchange(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Exchange creado", "name", dto.getName()));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> eliminar(@PathVariable String name) {
        adminService.eliminarExchange(name);
        return ResponseEntity.noContent().build();
    }
}