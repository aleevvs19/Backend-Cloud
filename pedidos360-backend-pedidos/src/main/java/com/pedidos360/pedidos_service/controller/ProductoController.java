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
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return ResponseEntity.ok(productoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<Producto>> obtenerPorSucursal(
            @PathVariable Long sucursalId) {

        return ResponseEntity.ok(
                productoRepository.findBySucursalId(sucursalId)
        );
    }

    @PostMapping
    public ResponseEntity<Producto> crear(
            @RequestBody Producto producto) {

        producto.setId(null);

        return ResponseEntity.ok(
                productoRepository.save(producto)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable Long id,
            @RequestBody Producto producto) {

        return productoRepository.findById(id)
                .map(productoExistente -> {

                    productoExistente.setNombre(producto.getNombre());
                    productoExistente.setDescripcion(producto.getDescripcion());
                    productoExistente.setPrecio(producto.getPrecio());
                    productoExistente.setStock(producto.getStock());
                    productoExistente.setSucursalId(producto.getSucursalId());

                    return ResponseEntity.ok(
                            productoRepository.save(productoExistente)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        productoRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}