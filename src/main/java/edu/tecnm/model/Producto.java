package edu.tecnm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="producto")
public class Producto {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;
    
    private String nombre;
    private String descripcion;
    private double precio;
    private String tipo;
    
    @Column(name = "fotoProducto", length = 255, nullable = true)
    private String fotoProducto;

    public Integer getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getFotoProducto() {
        return fotoProducto;
    }
    public void setFotoProducto(String fotoProducto) {
        this.fotoProducto = fotoProducto;
    }

    @Override
    public String toString() {
        return "Producto [idProducto=" + idProducto + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", precio=" + precio + ", tipo=" + tipo + ", fotoProducto=" + fotoProducto + "]";
    }
}
