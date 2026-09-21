package com.pedidos360.pedidos_service;

import com.pedidos360.pedidos_service.entity.Producto;
import com.pedidos360.pedidos_service.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarProductos(ProductoRepository productoRepository) {

        return args -> {

            if (productoRepository.count() > 0) {
                return;
            }

            productoRepository.save(
                Producto.builder()
                    .nombre("Baguette Rústica Madre")
                    .descripcion("Pan artesanal de masa madre.")
                    .precio(new BigDecimal("1500"))
                    .stock(24)
                    .sucursalId(1L)
                    .build()
            );

            productoRepository.save(
                Producto.builder()
                    .nombre("Croissant de Almendras")
                    .descripcion("Croissant artesanal relleno de almendras.")
                    .precio(new BigDecimal("2200"))
                    .stock(15)
                    .sucursalId(1L)
                    .build()
            );

            productoRepository.save(
                Producto.builder()
                    .nombre("Iced Latte Caramelo")
                    .descripcion("Café frío con leche y caramelo.")
                    .precio(new BigDecimal("2800"))
                    .stock(30)
                    .sucursalId(2L)
                    .build()
            );

            productoRepository.save(
                Producto.builder()
                    .nombre("Kuchen de Nuez")
                    .descripcion("Kuchen tradicional con nueces.")
                    .precio(new BigDecimal("16500"))
                    .stock(5)
                    .sucursalId(1L)
                    .build()
            );

            productoRepository.save(
                Producto.builder()
                    .nombre("Café Americano")
                    .descripcion("Café americano de especialidad.")
                    .precio(new BigDecimal("1800"))
                    .stock(20)
                    .sucursalId(2L)
                    .build()
            );

            System.out.println("======================================");
            System.out.println("PRODUCTOS INICIALES CARGADOS");
            System.out.println("Cantidad: " + productoRepository.count());
            System.out.println("======================================");
        };
    }
}