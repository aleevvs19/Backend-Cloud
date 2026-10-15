package com.duoc.pedidos360.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ExchangeRequestDTO {

    @NotBlank(message = "El nombre del exchange es obligatorio")
    @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
    @Pattern(regexp = "^(?!amq\\.)[A-Za-z0-9._-]+$",
             message = "Nombre inválido: use letras, números, '.', '_' o '-' y no empiece con 'amq.'")
    private String name;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "^(direct|topic|fanout|headers)$",
             message = "Tipo inválido: use direct, topic, fanout o headers")
    private String type;

    private boolean durable = true;
    private boolean autoDelete = false;
}