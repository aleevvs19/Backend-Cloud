package com.pedidos360.notificaciones.dto;

public class PedidoEventDTO {
    
    private Long idPedido;
    private String correoCliente;
    private String estado;
    private Double total;

    // Constructor vacío obligatorio para que Jackson convierta el JSON
    public PedidoEventDTO() {
    }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public String getCorreoCliente() { return correoCliente; }
    public void setCorreoCliente(String correoCliente) { this.correoCliente = correoCliente; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    
    @Override
    public String toString() {
        return "Pedido #" + idPedido + " | Estado: " + estado + " | Cliente: " + correoCliente + " | Total: $" + total;
    }
}