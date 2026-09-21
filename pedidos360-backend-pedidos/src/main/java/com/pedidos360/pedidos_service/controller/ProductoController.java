package com.pedidos360.pedidos_service.controller;

import com.pedidos360.pedidos_service.entity.Producto;
import com.pedidos360.pedidos_service.repository.ProductoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerProductos() {
        return ResponseEntity.ok(productoRepository.findAll());
    }

    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<Producto>> obtenerProductosPorSucursal(
            @PathVariable Long sucursalId) {

        return ResponseEntity.ok(
                productoRepository.findBySucursalId(sucursalId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(
            @PathVariable Long id) {

        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}