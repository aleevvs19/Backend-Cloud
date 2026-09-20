package com.pedidos360.pedidos_service.config;

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
                            .descripcion("Pan artesanal de fermentación natural de 24 horas.")
                            .precio(new BigDecimal("1500"))
                            .stock(24)
                            .sucursalId(1L)
                            .build()
            );

            productoRepository.save(
                    Producto.builder()
                            .nombre("Croissant de Almendras")
                            .descripcion("Croissant relleno de frangipane y almendras tostadas.")
                            .precio(new BigDecimal("2200"))
                            .stock(15)
                            .sucursalId(1L)
                            .build()
            );

            productoRepository.save(
                    Producto.builder()
                            .nombre("Iced Latte Caramelo")
                            .descripcion("Espresso doble, leche fría y caramelo artesanal.")
                            .precio(new BigDecimal("2800"))
                            .stock(30)
                            .sucursalId(1L)
                            .build()
            );

            productoRepository.save(
                    Producto.builder()
                            .nombre("Kuchen de Nuez Tradicional")
                            .descripcion("Receta tradicional con nueces seleccionadas.")
                            .precio(new BigDecimal("16500"))
                            .stock(5)
                            .sucursalId(1L)
                            .build()
            );

            productoRepository.save(
                    Producto.builder()
                            .nombre("Café Espresso Doble")
                            .descripcion("Café de especialidad tostado en origen.")
                            .precio(new BigDecimal("1900"))
                            .stock(20)
                            .sucursalId(1L)
                            .build()
            );

            productoRepository.save(
                    Producto.builder()
                            .nombre("Pan de Molde Integral")
                            .descripcion("Pan integral con semillas de girasol, lino y sésamo.")
                            .precio(new BigDecimal("2400"))
                            .stock(18)
                            .sucursalId(1L)
                            .build()
            );

            productoRepository.save(
                    Producto.builder()
                            .nombre("Torta Tres Leches")
                            .descripcion("Bizcocho húmedo con mezcla de tres leches y merengue.")
                            .precio(new BigDecimal("18900"))
                            .stock(8)
                            .sucursalId(1L)
                            .build()
            );

            productoRepository.save(
                    Producto.builder()
                            .nombre("Jugo Natural de Frambuesa")
                            .descripcion("Jugo natural de frambuesa sin azúcar añadida.")
                            .precio(new BigDecimal("2500"))
                            .stock(12)
                            .sucursalId(1L)
                            .build()
            );

            System.out.println("=========================================");
            System.out.println("PRODUCTOS INICIALES CARGADOS");
            System.out.println("Cantidad: " + productoRepository.count());
            System.out.println("=========================================");
        };
    }
}