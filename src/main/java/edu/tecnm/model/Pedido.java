package edu.tecnm.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;

    private LocalDate fecha;
    private Double total;

    @JsonManagedReference
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles;
    
    //Para cliente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "atender", // Nombre de la nueva tabla de union en la BD
        joinColumns = @JoinColumn(name = "idPedido"), // Columna que referencia a esta entidad
        inverseJoinColumns = @JoinColumn(name = "claveEmpleado") // Columna que referencia a la otra entidad
    )
    private Set<Empleado> empleadosQueAtienden = new HashSet<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idServicio", nullable = true)
    private Reservacion reservacion;

    public Integer getIdPedido() {
        return idPedido;
    }
    public void setIdPedido(Integer id) {
        this.idPedido = idPedido;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
    
    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }
    
    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public Set<Empleado> getEmpleadosQueAtienden() {
        return empleadosQueAtienden;
    }

    public void setEmpleadosQueAtienden(Set<Empleado> empleados) {
        this.empleadosQueAtienden = empleados;
    }
    
    public Reservacion getReservacion() {
        return reservacion;
    }

    public void setReservacion(Reservacion reservacion) {
        this.reservacion = reservacion;
    }
}