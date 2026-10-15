package com.duoc.pedidos360.admin.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> validacion(BindException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        Map<String, Object> body = cuerpo(HttpStatus.BAD_REQUEST, "Parámetros inválidos");
        body.put("errores", errores);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> argumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(cuerpo(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(AmqpConnectException.class)
    public ResponseEntity<Map<String, Object>> sinConexion(AmqpConnectException ex) {
        log.error("No hay conexión con RabbitMQ", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(cuerpo(HttpStatus.SERVICE_UNAVAILABLE, "RabbitMQ no está disponible"));
    }

    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<Map<String, Object>> errorRabbit(AmqpException ex) {
        log.error("Error de RabbitMQ", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo(HttpStatus.CONFLICT,
                "RabbitMQ rechazó la operación (¿recurso existente con otra configuración?)"));
    }

    private Map<String, Object> cuerpo(HttpStatus status, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("mensaje", mensaje);
        return body;
    }
}