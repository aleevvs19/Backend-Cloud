
package com.pedidos360.pedidos_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

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

    // Configuracion JWT desde application.yml
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    // Identificador de la API registrada en Microsoft Entra ID
    private static final String API_CLIENT_ID =
        "501e8863-bd34-48ca-98ad-2c8ff1f8873d";

    private static final String API_SCOPE =
        "Ale-Pedidos360";

    // Validacion del token JWT
    @Bean
    public JwtDecoder jwtDecoder() {

        // Verifica la firma usando las claves publicas de Microsoft
        NimbusJwtDecoder decoder = NimbusJwtDecoder
            .withJwkSetUri(jwkSetUri)
            .build();

        // Verifica que el token este destinado a Pedidos360
        JwtClaimValidator<List<String>> audienceValidator =
            new JwtClaimValidator<>("aud", audience ->
                audience != null &&
                (
                    audience.contains(API_CLIENT_ID) ||
                    audience.contains("api://" + API_CLIENT_ID)
                )
            );

        // Verifica que el token tenga el permiso de nuestra API
        JwtClaimValidator<String> scopeValidator =
            new JwtClaimValidator<>("scp", scope ->
                scope != null &&
                Arrays.asList(scope.split("\\s+"))
                    .contains(API_SCOPE)
            );

        // Valida emisor, expiracion, audiencia y scope
        OAuth2TokenValidator<Jwt> validator =
            new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuerUri),
                audienceValidator,
                scopeValidator
            );

        decoder.setJwtValidator(validator);

        return decoder;
    }

    // Reglas de seguridad para las rutas HTTP
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {

        http
            .cors(cors ->
                cors.configurationSource(corsConfigurationSource())
            )

            // API REST autenticada con token Bearer
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Bloquear consola H2 por HTTP
                .requestMatchers("/h2-console/**")
                    .denyAll()

                // Endpoint de salud
                .requestMatchers("/actuator/health")
                    .permitAll()

                // Otros endpoints Actuator
                .requestMatchers("/actuator/**")
                    .hasAuthority(
                        "ROLE_Administrador_General"
                    )

                // Crear pedidos
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/pedidos"
                )
                    .authenticated()

                // Consultar pedidos del usuario autenticado
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos/mis-pedidos"
                )
                    .authenticated()

                // Consultar pedidos de una sucursal
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos/sucursal/**"
                )
                    .hasAnyAuthority(
                        "ROLE_Operador_Cocina",
                        "ROLE_Administrador_Local",
                        "ROLE_Administrador_General"
                    )

                // Cambiar el estado de un pedido
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/pedidos/*/estado"
                )
                    .hasAnyAuthority(
                        "ROLE_Operador_Cocina",
                        "ROLE_Administrador_Local",
                        "ROLE_Administrador_General"
                    )

                // Rutas de cocina
                .requestMatchers("/api/pedidos/cocina/**")
                    .hasAnyAuthority(
                        "ROLE_Operador_Cocina",
                        "ROLE_Administrador_Local",
                        "ROLE_Administrador_General"
                    )

                // Rutas administrativas
                .requestMatchers("/api/pedidos/admin/**")
                    .hasAnyAuthority(
                        "ROLE_Administrador_Local",
                        "ROLE_Administrador_General"
                    )

                // Bloquear cualquier otra ruta de pedidos
                .requestMatchers(
                    "/api/pedidos",
                    "/api/pedidos/**"
                )
                    .denyAll()

                // Productos y demas endpoints:
                // requieren autenticacion
                .anyRequest()
                    .authenticated()
            )

            // Validacion JWT y conversion de roles
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(
                        jwtAuthenticationConverter()
                    )
                )
            );

        return http.build();
    }

    // CORS para el frontend Angular
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
            new CorsConfiguration();

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
                "PATCH",
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

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }

    // Convertir roles de Microsoft Entra ID
    // en authorities de Spring Security
    private Converter<Jwt, AbstractAuthenticationToken>
        jwtAuthenticationConverter() {

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
