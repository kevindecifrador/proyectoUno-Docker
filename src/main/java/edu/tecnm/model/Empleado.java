package edu.tecnm.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "empleado")
public class Empleado {

    @Id
    private String clave; // la PK es un String (Varchar)

    private String nombreCompleto;
    private String puesto;
    
    @ManyToMany(mappedBy = "empleadosQueAtienden")
    @JsonIgnore
    private Set<Pedido> pedidosAtendidos = new HashSet<>();
    
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }
    
    public Set<Pedido> getPedidosAtendidos() {
        return pedidosAtendidos;
    }

    public void setPedidosAtendidos(Set<Pedido> pedidosAtendidos) {
        this.pedidosAtendidos = pedidosAtendidos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}