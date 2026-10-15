package com.duoc.pedidos360.admin.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duoc.pedidos360.admin.dto.BindingRequestDTO;
import com.duoc.pedidos360.admin.service.RabbitAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/bindings")
@RequiredArgsConstructor
public class BindingController {

    private final RabbitAdminService adminService;

    @PostMapping
    public ResponseEntity<Map<String, String>> crear(@Valid @RequestBody BindingRequestDTO dto) {
        adminService.crearBinding(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Binding creado"));
    }

    // DELETE /bindings?exchange=..&destination=..&destinationType=QUEUE&routingKey=..
    @DeleteMapping
    public ResponseEntity<Void> eliminar(@Valid @ModelAttribute BindingRequestDTO dto) {
        adminService.eliminarBinding(dto);
        return ResponseEntity.noContent().build();
    }
}