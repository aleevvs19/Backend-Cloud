package com.duoc.pedidos360.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BindingRequestDTO {

    @NotBlank(message = "El destino (cola o exchange) es obligatorio")
    private String destination;

    @NotBlank(message = "El tipo de destino es obligatorio")
    @Pattern(regexp = "^(QUEUE|EXCHANGE)$", message = "destinationType debe ser QUEUE o EXCHANGE")
    private String destinationType = "QUEUE";

    @NotBlank(message = "El exchange de origen es obligatorio")
    private String exchange;

    private String routingKey = "";
}