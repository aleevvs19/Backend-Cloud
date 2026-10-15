package com.pedidos360.rabbitadmin;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController public class RabbitAdminController {
 private final RabbitAdminService service;
 private static final String VALID_NAME="[A-Za-z0-9_.-]{1,120}";
 public RabbitAdminController(RabbitAdminService service){this.service=service;}
 public record NameRequest(@NotBlank @Pattern(regexp=VALID_NAME) String name) {}
 public record BindingRequest(@NotBlank @Pattern(regexp=VALID_NAME) String queue,
   @NotBlank @Pattern(regexp=VALID_NAME) String exchange,
   @NotBlank @Pattern(regexp="[A-Za-z0-9_.#*-]{1,120}") String routingKey) {}
 @PostMapping("/queues") public ResponseEntity<Void> queue(@Valid @RequestBody NameRequest dto){service.createQueue(dto.name());return ResponseEntity.status(201).build();}
 @DeleteMapping("/queues/{name}") public ResponseEntity<Void> deleteQueue(@PathVariable @Pattern(regexp=VALID_NAME) String name){service.deleteQueue(name);return ResponseEntity.noContent().build();}
 @PostMapping("/exchanges") public ResponseEntity<Void> exchange(@Valid @RequestBody NameRequest dto){service.createExchange(dto.name());return ResponseEntity.status(201).build();}
 @DeleteMapping("/exchanges/{name}") public ResponseEntity<Void> deleteExchange(@PathVariable @Pattern(regexp=VALID_NAME) String name){service.deleteExchange(name);return ResponseEntity.noContent().build();}
 @PostMapping("/bindings") public ResponseEntity<Void> binding(@Valid @RequestBody BindingRequest dto){service.createBinding(dto.queue(),dto.exchange(),dto.routingKey());return ResponseEntity.status(201).build();}
 @DeleteMapping("/bindings") public ResponseEntity<Void> deleteBinding(@Valid @RequestBody BindingRequest dto){service.deleteBinding(dto.queue(),dto.exchange(),dto.routingKey());return ResponseEntity.noContent().build();}
}
