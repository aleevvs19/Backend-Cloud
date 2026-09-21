package com.pedidos360.pedidos_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // CSRF desactivado para la API
            .csrf(csrf -> csrf.disable())

            // Permisos
            .authorizeHttpRequests(auth -> auth
                // Consola H2 para desarrollo local
                .requestMatchers("/h2-console/**").permitAll()

                // Actuator
                .requestMatchers("/actuator/**").permitAll()

                // Roles de cocina
                .requestMatchers("/api/pedidos/cocina/**")
                    .hasAnyAuthority(
                        "ROLE_Operador_Cocina",
                        "ROLE_Administrador_Local",
                        "ROLE_Administrador_General"
                    )

                // Roles de administración
                .requestMatchers("/api/pedidos/admin/**")
                    .hasAnyAuthority(
                        "ROLE_Administrador_Local",
                        "ROLE_Administrador_General"
                    )

                // Pedidos requieren autenticación
                .requestMatchers("/api/pedidos/**").authenticated()

                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )

            // Permitir que la consola H2 se muestre correctamente
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )

            // OAuth2 Resource Server / JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    // CORS para Angular (Local y AWS EC2)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
            Arrays.asList(
                "http://localhost:4200",
                "http://100.50.157.1",
                "https://100.50.157.1",
                "http://100.50.157.1:8080",
                "https://100.50.157.1:8080"
            )
        );

        configuration.setAllowedMethods(
            Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            Arrays.asList("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // Convierte los roles de Microsoft Entra ID
    // en authorities de Spring Security
    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {

        JwtAuthenticationConverter jwtConverter =
            new JwtAuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {

            List<String> roles =
                jwt.getClaimAsStringList("roles");

            if (roles == null || roles.isEmpty()) {
                return Collections.emptyList();
            }

            return roles.stream()
                .map(role ->
                    new SimpleGrantedAuthority("ROLE_" + role)
                )
                .collect(Collectors.toList());
        });

        return jwtConverter;
    }
}