package com.duoc.pedidos360.admin.controller;

import java.util.Map;
import java.util.Properties;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duoc.pedidos360.admin.dto.QueueRequestDTO;
import com.duoc.pedidos360.admin.service.RabbitAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/queues")
@RequiredArgsConstructor
public class QueueController {

    private final RabbitAdminService adminService;

    @PostMapping
    public ResponseEntity<Map<String, String>> crear(@Valid @RequestBody QueueRequestDTO dto) {
        adminService.crearCola(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Cola creada", "name", dto.getName()));
    }

    @GetMapping("/{name}")
    public ResponseEntity<Properties> obtener(@PathVariable String name) {
        return ResponseEntity.ok(adminService.obtenerCola(name));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> eliminar(@PathVariable String name) {
        adminService.eliminarCola(name);
        return ResponseEntity.noContent().build();
    }
}