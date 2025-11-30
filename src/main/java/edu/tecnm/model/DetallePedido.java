package edu.tecnm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="detalles_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetallePedido;

    private Integer cantidad;
    private Double precio;
    
 // RELACIÓN: Muchos detalles pertenecen a un pedido.
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="pedido_id")
    private Pedido pedido;

    // RELACIÓN: Muchos detalles apuntan a un producto.
    @ManyToOne
    @JoinColumn(name="producto_id")
    private Producto producto;

    public Integer getId() {
        return idDetallePedido;
    }
    public void setId(Integer id) {
        this.idDetallePedido = id;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public Double getPrecio() {
        return precio;
    }
    public void setPrecio(Double precio) {
        this.precio = precio;
    }
    
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    // MÉTODO FALTANTE PARA CALCULAR:
    public Double getSubtotal() {
        if (this.cantidad == null || this.precio == null) {
            return 0.0;
        }
        return this.cantidad * this.precio;
    }

}