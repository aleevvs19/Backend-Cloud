package com.duoc.pedidos360.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QueueRequestDTO {

    @NotBlank(message = "El nombre de la cola es obligatorio")
    @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
    @Pattern(regexp = "^(?!amq\\.)[A-Za-z0-9._-]+$",
             message = "Nombre inválido: use letras, números, '.', '_' o '-' y no empiece con 'amq.'")
    private String name;

    private boolean durable = true;
    private boolean autoDelete = false;

    @Pattern(regexp = "^$|^[A-Za-z0-9._-]+$", message = "deadLetterExchange inválido")
    private String deadLetterExchange;

    private String deadLetterRoutingKey;

    @Min(value = 1, message = "messageTtlMs debe ser mayor que 0")
    private Integer messageTtlMs;
}