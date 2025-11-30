package edu.tecnm.dto;

import java.util.List;

public class PedidoRequestDto {

    private Integer clienteId;
    private List<CartItemDto> items;
    
    private String empleadoClave;
    private Integer reservacionId;

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }
    
    public String getEmpleadoClave() { return empleadoClave; }
    public void setEmpleadoClave(String empleadoClave) { this.empleadoClave = empleadoClave; }

    public Integer getReservacionId() { return reservacionId; }
    public void setReservacionId(Integer reservacionId) { this.reservacionId = reservacionId; }
}